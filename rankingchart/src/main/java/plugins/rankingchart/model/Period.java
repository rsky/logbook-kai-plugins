package plugins.rankingchart.model;

import plugins.rankingchart.util.DateTimeUtil;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * ランキング期間
 */
public class Period {
    private String name;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public Period(String name, ZonedDateTime from, ZonedDateTime to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

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
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getFrom() {
        return from;
    }

    public ZonedDateTime getTo() {
        return to;
    }
}
