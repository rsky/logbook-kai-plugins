package plugins.rankingchart.model;

public enum ChartMode {
    SINGLE, MOM, QOQ, YOY;

    @Override
    public String toString() {
        switch (this) {
            case SINGLE:
                return "単月";
            case MOM:
                return "前月比";
            case QOQ:
                return "前期比";
            case YOY:
                return "前年比";
        }
        return name();
    }

    public String legendSuffix() {
        switch (this) {
            case SINGLE:
                return " (当月)";
            case MOM:
                return " (前月)";
            case QOQ:
                return " (前期)";
            case YOY:
                return " (前年)";
        }
        return "";
    }
}
