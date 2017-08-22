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
import plugins.rankingchart.bean.RankingRow;
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

    /** 最終戦果 */
    private RankingRow latestRanking;

    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        final String uri = requestMetaData.getRequestURI();
        try {
            if ((memberId == 0 || nickname == null) && uri.equals("/kcsapi/api_get_member/record")) {
                storeMemberId(jsonObject);
            }

            if (memberId > 0 && uri.equals("/kcsapi/api_req_ranking/mxltvkpyuklh")) {
                logRanking(jsonObject);
            }
        } catch (Exception e) {
            LoggerHolder.LOG.error(e.getMessage(), e);
        }
    }

    private void logRanking(JsonObject jsonObject) {
        final JsonValue data = getData(jsonObject);
        if (!(data instanceof JsonObject)) {
            return;
        }

        final JsonArray list = ((JsonObject) data).getJsonArray("api_list");
        if (list == null) {
            return;
        }

        if (latestRanking == null) {
            //csvManager.load();
            latestRanking = RankingDataManager.getDefault().getLatest();
        }

        final ZonedDateTime dateTime = DateTimeUtil.getRankingDateTime();
        if (latestRanking == null || !latestRanking.getDateTime().equals(dateTime)) {
            latestRanking = RankingRow.withDateTime(dateTime);
        }

        final RankingChartConfig config = RankingChartConfig.get();
        final int userRateFactor = /*config.getUserRateFactor()*/ 26;
        final long lastObfuscatedRate = config.getLastObfuscatedRate();
        boolean rankingUpdated = false;
        boolean configUpdated = false;

        for (JsonValue value : list) {
            if (value instanceof JsonObject) {
                final RankingListItem item = new RankingListItem((JsonObject) value);
                final int rankNo = item.getNo();
                final int rate = Calculator.calcRate(item.getNo(), item.getObfuscatedRate(), userRateFactor);

                if (rate != Calculator.NO_RATE) {
                    switch (rankNo) {
                        case 1:
                            latestRanking.setRank1(rate);
                            rankingUpdated = true;
                            break;
                        case 5:
                            latestRanking.setRank5(rate);
                            rankingUpdated = true;
                            break;
                        case 20:
                            latestRanking.setRank20(rate);
                            rankingUpdated = true;
                            break;
                        case 100:
                            latestRanking.setRank100(rate);
                            rankingUpdated = true;
                            break;
                        case 500:
                            latestRanking.setRank500(rate);
                            rankingUpdated = true;
                            break;
                    }

                    if (nickname != null && nickname.equals(item.getNickname())) {
                        latestRanking.setRankNo(rankNo);
                        latestRanking.setRate(rate);
                        rankingUpdated = true;
                    }
                }

                if (nickname != null && nickname.equals(item.getNickname())) {
                    final long obfuscatedRate = item.getObfuscatedRate();
                    if (rankNo != config.getLastRankNo() || obfuscatedRate != lastObfuscatedRate) {
                        config.setLastRankNo(rankNo);
                        config.setLastObfuscatedRate(obfuscatedRate);
                        configUpdated = true;
                    }
                }
            }
        }

        if (rankingUpdated) {
            RankingDataManager.getDefault().update(latestRanking);
        }

        if (configUpdated) {
            ThreadManager.getExecutorService().execute(Config.getDefault()::store);
        }
    }

    private void storeMemberId(JsonObject jsonObject) {
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
