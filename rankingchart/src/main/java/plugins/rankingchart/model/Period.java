package plugins.rankingchart.model;

import java.time.ZonedDateTime;

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
