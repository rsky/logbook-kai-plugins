package plugins.rankingchart.model;

public enum ChartMode {
    SINGLE, MOM, QOQ, YOY;

    @Override
    public String toString() {
        return switch (this) {
            case SINGLE -> "単月";
            case MOM -> "前月比";
            case QOQ -> "前期比";
            case YOY -> "前年比";
        };
    }

    public String legendSuffix() {
        return switch (this) {
            case SINGLE -> " (当月)";
            case MOM -> " (前月)";
            case QOQ -> " (前期)";
            case YOY -> " (前年)";
        };
    }
}
