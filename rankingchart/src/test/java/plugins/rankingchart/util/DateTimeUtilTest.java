package plugins.rankingchart.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("WeakerAccess")
@Tag("util")
public class DateTimeUtilTest {
    private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

    @Test
    @DisplayName("日時を前日の15時に丸める")
    public void truncateToRankingDateTimeYesterdayPM() {
        ZonedDateTime expected = ZonedDateTime.of(1999, 12, 31, 15, 0, 0, 0, JST);
        ZonedDateTime earlier = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, JST);
        ZonedDateTime later = ZonedDateTime.of(2000, 1, 1, 2, 59, 59, 999999, JST);
        assertEquals(expected, DateTimeUtil.truncateToRankingDateTime(earlier));
        assertEquals(expected, DateTimeUtil.truncateToRankingDateTime(later));
    }

    @Test
    @DisplayName("日時を当日の3時に丸める")
    public void truncateToRankingDateTimeTodayAM() {
        ZonedDateTime expected = ZonedDateTime.of(2000, 1, 1, 3, 0, 0, 0, JST);
        ZonedDateTime earlier = ZonedDateTime.of(2000, 1, 1, 3, 0, 0, 0, JST);
        ZonedDateTime later = ZonedDateTime.of(2000, 1, 1, 14, 59, 59, 999999, JST);
        assertEquals(expected, DateTimeUtil.truncateToRankingDateTime(earlier));
        assertEquals(expected, DateTimeUtil.truncateToRankingDateTime(later));
    }

    @Test
    @DisplayName("日時を当日の15時に丸める")
    public void truncateToRankingDateTimeTodayPM() {
        ZonedDateTime expected = ZonedDateTime.of(2000, 1, 1, 15, 0, 0, 0, JST);
        ZonedDateTime earlier = ZonedDateTime.of(2000, 1, 1, 15, 0, 0, 0, JST);
        ZonedDateTime later = ZonedDateTime.of(2000, 1, 1, 23, 59, 59, 999999, JST);
        assertEquals(expected, DateTimeUtil.truncateToRankingDateTime(earlier));
        assertEquals(expected, DateTimeUtil.truncateToRankingDateTime(later));
    }
}
