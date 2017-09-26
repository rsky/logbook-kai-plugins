package plugins.rankingchart.bean;

import java.time.ZonedDateTime;

/**
 * ランキング期間
 */
public class Period {
    private String name;
    private Over over;
    private ZonedDateTime from;
    private ZonedDateTime to;

    public Period(String name, ZonedDateTime from, ZonedDateTime to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public Period(Over over) {
        this.over = over;
        this.name = String.format("%s比", over);
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

    public Over getOver() {
        return over;
    }

    public enum Over {
        YEAR,
        QUARTER,
        MONTH;

        @Override
        public String toString() {
            switch (this) {
                case YEAR:
                    return "前年";
                case QUARTER:
                    return "前期";
                case MONTH:
                    return "前月";
            }
            return super.toString();
        }
    }
}
