package plugins.rankingchart.api;

import logbook.api.APIListenerSpi;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingChartConfig;
import plugins.rankingchart.bean.RankingEntry;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.util.Calculator;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.rankingchart.util.RankingDataManager;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class RankingListener implements APIListenerSpi {

    /** 提督ID */
    private int memberId = 0;

    /** 提督ニックネーム */
    private String nickname;

    /** 現在の戦果 */
    private RankingLogItem ranking;

    /** 1〜100,500位の戦果 */
    private Map<Integer, Long> rankingSource = new HashMap<>();

    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        final String uri = requestMetaData.getRequestURI();
        try {
            if ((memberId == 0 || nickname == null) && uri.equals("/kcsapi/api_get_member/record")) {
                storeMemberInfo(jsonObject);
            }

            if (memberId > 0 && uri.equals("/kcsapi/api_req_ranking/mxltvkpyuklh")) {
                logRanking(jsonObject);
            }
        } catch (Exception e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    /**
     * ランキングをデータベースに記録する
     * @param jsonObject APIレスポンス
     */
    private void logRanking(JsonObject jsonObject) {
        final JsonValue data = getData(jsonObject);
        if (!(data instanceof JsonObject)) {
            return;
        }

        final JsonArray list = ((JsonObject) data).getJsonArray("api_list");
        if (list == null) {
            return;
        }

        if (ranking == null) {
            ranking = RankingDataManager.getDefault().getLatest();
        }

        final ZonedDateTime dateTime = DateTimeUtil.getRankingDateTime();
        // 基準となる日時が変わっていたら現在のランキングも新しい日時で作り直す
        if (ranking == null || !ranking.getDateTime().equals(dateTime)) {
            ranking = RankingLogItem.withDateTime(dateTime);
            rankingSource.clear();
        }

        final RankingChartConfig config = RankingChartConfig.get();
        int userRateFactor = config.getUserRateFactor();
        final long lastObfuscatedRate = config.getLastObfuscatedRate();
        final int rankingSourceSize = rankingSource.size();
        boolean rankingUpdated = false;
        boolean configUpdated = false;

        for (JsonValue value : list) {
            final RankingEntry item;
            if (value instanceof JsonObject) {
                item = new RankingEntry((JsonObject) value);
            } else {
                // 実際はここには到達しないが便宜上
                break;
            }

            final int rankNo = item.getNo();
            final long obfuscatedRate = item.getObfuscatedRate();
            final int rate = Calculator.calcRate(rankNo, obfuscatedRate, userRateFactor);

            // 戦果係数がセットされており、戦果のデコードに成功した場合
            if (rate != Calculator.NO_RATE) {
                // 1位とランキングボーダーの戦果が含まれていれば更新する
                if (ranking.put(rankNo, rate)) {
                    rankingUpdated = true;
                }

                // 自分の戦果が含まれていれば更新する
                if (nickname != null && nickname.equals(item.getNickname())) {
                    ranking.setRankNo(rankNo);
                    ranking.setRate(rate);
                    rankingUpdated = true;
                }
            }

            // 戦果係数を手動で導出するために自分の順位と難読化された戦果を保存する
            if (nickname != null && nickname.equals(item.getNickname())) {
                if (rankNo != config.getLastRankNo() || obfuscatedRate != lastObfuscatedRate) {
                    config.setLastRankNo(rankNo);
                    config.setLastObfuscatedRate(obfuscatedRate);
                    configUpdated = true;
                }
            }

            // 戦果係数を自動で導出するために100位までと500位の戦果を保持する
            if ((1 <= rankNo && rankNo <= 100) || rankNo == 500) {
                rankingSource.put(rankNo, obfuscatedRate);
            }
        }

        // 1〜100位(+500位)のデータが揃ったら戦果係数を自動で求める
        if (rankingSourceSize < 100 && rankingSource.size() >= 100) {
            int autoUserRateFactor = Calculator.detectUserRateFactor(rankingSource);
            if (autoUserRateFactor != 0 && autoUserRateFactor != userRateFactor) {
                // 戦果係数が更新されていたら保存する
                config.setUserRateFactor(autoUserRateFactor);
                configUpdated = true;

                bulkSetRanking(config);
                rankingUpdated = true;
            }
        }

        if (rankingUpdated) {
            RankingDataManager.getDefault().update(ranking);
        }

        if (configUpdated) {
            ThreadManager.getExecutorService().execute(Config.getDefault()::store);
        }
    }

    /**
     * 戦果係数更新前に取得したデータから一括で現在の戦果を更新する
     *
     * @param config 戦果チャート構成情報
     */
    private void bulkSetRanking(RankingChartConfig config) {
        int userRateFactor = config.getUserRateFactor();
        int myRankNo = config.getLastRankNo();
        if (myRankNo > 0) {
            ranking.setRankNo(myRankNo);
            ranking.setRate(Calculator.calcRate(myRankNo, config.getLastObfuscatedRate(), userRateFactor));
        }

        IntStream.of(1, 5, 20, 100, 500)
                .filter(rankingSource::containsKey)
                .forEach(rankNo -> {
                    int rate = Calculator.calcRate(rankNo, rankingSource.get(rankNo), userRateFactor);
                    ranking.put(rankNo, rate);
                });
    }

    /**
     * 自分の戦果を特定するのに必要なニックネーム等の情報を取得・保持する
     * @param jsonObject APIレスポンス
     */
    private void storeMemberInfo(JsonObject jsonObject) {
        final JsonValue data = getData(jsonObject);
        if (!(data instanceof JsonObject)) {
            return;
        }

        final JsonObject obj = (JsonObject) data;

        if (obj.containsKey("api_member_id")) {
            memberId = obj.getInt("api_member_id");
        }

        if (obj.containsKey("api_nickname")) {
            nickname = obj.getString("api_nickname");
        }
    }

    /**
     * APIリザルトからapi_dataフィールドの値を抜き出す
     * @param jsonObject APIレスポンス
     * @return APIレスポンスのapi_dataフィールド（レスポンスデータ本体）
     */
    private JsonValue getData(JsonObject jsonObject) {
        if (jsonObject.getInt("api_result") != 1) {
            return null;
        }

        return jsonObject.get("api_data");
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingListener.class);
    }
}
