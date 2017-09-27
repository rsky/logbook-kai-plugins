package plugins.rankingchart.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {
    /** タイムゾーン(JST) */
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    /** 年月のフォーマット */
    private static final DateTimeFormatter FORMATTER_MONTH = DateTimeFormatter.ofPattern("yyyy年M月");

    /** 日付のフォーマット */
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("M月d日");

    /** 日時のフォーマット */
    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    /**
     * JSTの現在時刻を取得する
     *
     * @return JSTの現在時刻
     */
    public static ZonedDateTime now() {
        return ZonedDateTime.now(JST);
    }

    /**
     * タイムゾーンをJSTとして現在時刻を3時または15時に丸めた日時を取得する
     *
     * @return ランキングが確定した日時
     */
    public static ZonedDateTime rankingDateTime() {
        return truncateToRankingDateTime(now());
    }

    /**
     * 日時を3時または15時に丸める
     *
     * @param dateTime 日時
     * @return ランキングが確定した日時
     */
    static ZonedDateTime truncateToRankingDateTime(ZonedDateTime dateTime) {
        if (dateTime.getHour() < 3) {
            // 3時まで→前日の15時
            dateTime = dateTime.minusDays(1).withHour(15);
        } else if (dateTime.getHour() < 15) {
            // 15時まで→当日の3時
            dateTime = dateTime.withHour(3);
        } else {
            // 15時以降→15時
            dateTime = dateTime.withHour(15);
        }
        return dateTime.truncatedTo(ChronoUnit.HOURS);
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
     * 日時文字列を日時に変換する
     *
     * @param text 日時文字列
     * @return JSTの日時
     */
    public static ZonedDateTime dateTimeFromString(String text) {
        return LocalDateTime.parse(text, FORMATTER_DATETIME).atZone(JST);
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
