package plugins.rankingchart.api;

import logbook.api.APIListenerSpi;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingChartConfig;
import plugins.rankingchart.bean.RankingListItem;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.util.Calculator;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.rankingchart.util.RankingDataManager;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.time.ZonedDateTime;

public class RankingListener implements APIListenerSpi {

    /** 提督ID */
    private int memberId = 0;

    /** 提督ニックネーム */
    private String nickname;

    /** 現在の戦果 */
    private RankingLogItem ranking;

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
        }

        final RankingChartConfig config = RankingChartConfig.get();
        final int userRateFactor = /*config.getUserRateFactor()*/ 26;
        final long lastObfuscatedRate = config.getLastObfuscatedRate();
        boolean rankingUpdated = false;
        boolean configUpdated = false;

        for (JsonValue value : list) {
            final RankingListItem item;
            if (value instanceof JsonObject) {
                item = new RankingListItem((JsonObject) value);
            } else {
                // 実際はここには到達しないが便宜上
                break;
            }

            final int rankNo = item.getNo();
            final int rate = Calculator.calcRate(item.getNo(), item.getObfuscatedRate(), userRateFactor);

            // 戦果係数がセットされており、戦果のデコードに成功した場合
            if (rate != Calculator.NO_RATE) {
                // 1位とランキングボーダーの戦果が含まれていれば更新する
                switch (rankNo) {
                    case 1:
                        ranking.setRank1(rate);
                        rankingUpdated = true;
                        break;
                    case 5:
                        ranking.setRank5(rate);
                        rankingUpdated = true;
                        break;
                    case 20:
                        ranking.setRank20(rate);
                        rankingUpdated = true;
                        break;
                    case 100:
                        ranking.setRank100(rate);
                        rankingUpdated = true;
                        break;
                    case 500:
                        ranking.setRank500(rate);
                        rankingUpdated = true;
                        break;
                }

                // 自分の戦果が含まれていれば更新する
                if (nickname != null && nickname.equals(item.getNickname())) {
                    ranking.setRankNo(rankNo);
                    ranking.setRate(rate);
                    rankingUpdated = true;
                }
            }

            // 戦果係数を導出するために自分の順位と難読化された戦果を保存する
            if (nickname != null && nickname.equals(item.getNickname())) {
                final long obfuscatedRate = item.getObfuscatedRate();
                if (rankNo != config.getLastRankNo() || obfuscatedRate != lastObfuscatedRate) {
                    config.setLastRankNo(rankNo);
                    config.setLastObfuscatedRate(obfuscatedRate);
                    configUpdated = true;
                }
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
