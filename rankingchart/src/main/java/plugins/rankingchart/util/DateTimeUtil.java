package plugins.rankingchart.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {
    /** タイムゾーン */
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    /** 年月のフォーマット */
    private static final DateTimeFormatter FORMATTER_MONTH = DateTimeFormatter.ofPattern("yyyy-MM");

    /** 日付のフォーマット */
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("M月d日");

    /** 日時のフォーマット */
    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
     * エポック秒を日時に変換する
     *
     * @param epoch エポック秒
     * @return JSTの日時
     */
    public static ZonedDateTime dateTimeFromEpoch(long epoch) {
        return Instant.ofEpochSecond(epoch).atZone(JST);
    }

    /**
     * @return グレゴリオ暦・JSTのカレンダー
     */
    public static Calendar getCalender() {
        return GregorianCalendar.getInstance(TimeZone.getTimeZone(JST));
    }

    /**
     * @param dateTime 日時
     * @return 年月をフォーマットした文字列
     */
    public static String formatMonth(ZonedDateTime dateTime) {
        return FORMATTER_MONTH.format(dateTime);
    }

    /**
     * @param dateTime 日時
     * @return 日付をフォーマットした文字列
     */
    public static String formatDate(ZonedDateTime dateTime) {
        return FORMATTER_DATE.format(dateTime);
    }

    /**
     * @param dateTime 日時
     * @return 日時をフォーマットした文字列
     */
    public static String formatDateTime(ZonedDateTime dateTime) {
        return FORMATTER_DATETIME.format(dateTime);
    }
}
