package plugins.rankingchart.model;

import lombok.NonNull;
import plugins.rankingchart.util.DateTimeUtil;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * ランキング期間
 */
public record Period(@NonNull String name, @NonNull ZonedDateTime from, @NonNull ZonedDateTime to) {
    public Period(ZonedDateTime from) {
        this(
                DateTimeUtil.formatMonth(from),
                from,
                from.with(TemporalAdjusters.lastDayOfMonth()).withHour(23)
        );
    }

    public Period with(ChartMode mode) {
        var dt = switch (mode) {
            case MOM -> from.minusMonths(1);
            case QOQ -> from.minusMonths(3);
            case YOY -> from.minusYears(1);
            default -> from;
        };

        return new Period(dt);
    }

    @Override
    @NonNull
    public String toString() {
        return name;
    }
}
