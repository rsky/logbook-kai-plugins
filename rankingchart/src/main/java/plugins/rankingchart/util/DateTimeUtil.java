package plugins.rankingchart.util;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {
    /** タイムゾーン */
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    /**
     * タイムゾーンをJSTとして3時または15時に丸めた日付/時間を取得する
     *
     * @return ランキングが確定した日付/時間
     */
    public static ZonedDateTime getRankingDateTime() {
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
        return date.withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * SQLのタイムスタンプをJSTの日時に変換する
     *
     * @param timestamp タイムスタンプ
     * @return JSTの日時
     */
    public static ZonedDateTime dateTimeFromTimestamp(Timestamp timestamp) {
        return timestamp.toInstant().atZone(JST);
    }

    /**
     * @return グレゴリオ暦・JSTのカレンダー
     */
    public static Calendar getCalender() {
        return GregorianCalendar.getInstance(TimeZone.getTimeZone(JST));
    }
}
