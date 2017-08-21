package plugins.rankingchart.api;

import logbook.api.APIListenerSpi;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

public class RankingListener implements APIListenerSpi {
    /** 日付書式 */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** タイムゾーン */
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        LoggerHolder.LOG.info(requestMetaData.getRequestURI());
        /*
        Map<String, Boolean> config = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jwf = Json.createWriterFactory(config);
        StringWriter sw = new StringWriter();
        try (JsonWriter jsonWriter = jwf.createWriter(sw)) {
            jsonWriter.writeObject(jsonObject);
        }

        LoggerHolder.LOG.info(sw.toString());
        */
    }

    /**
     * タイムゾーンをJSTとして3時または15時に丸めた日付/時間を"yyyy-MM-dd HH:mm"形式の文字列として取得します
     *
     * @return ランキングが確定した日付/時間
     */
    private static String rankingDateTimeString() {
        ZonedDateTime now = ZonedDateTime.now(JST);
        ZonedDateTime date;
        if (now.getHour() < 3) {
            // 3時まで→前日の15時
            date = now.minusDays(1).withHour(15);
        } else if (now.getHour() < 15) {
            // 15時まで→当日の3時
            date = now.withHour(3);
        } else {
            // 15時以降→15時
            date = now.withHour(15);
        }
        return DATE_FORMAT.format(date);
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingListener.class);
    }
}
