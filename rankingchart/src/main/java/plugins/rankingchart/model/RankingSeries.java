package plugins.rankingchart.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.ZonedDateTime;

public class RankingSeries {
    /** ランキング1位戦果 */
    private final XYChart.Series<Number, Number> rank1Series = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rank1Data = FXCollections.observableArrayList();
    private final BooleanProperty rank1Enabled = new SimpleBooleanProperty();

    /** ランキング5位戦果 */
    private final XYChart.Series<Number, Number> rank5Series = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rank5Data = FXCollections.observableArrayList();
    private final BooleanProperty rank5Enabled = new SimpleBooleanProperty();

    /** ランキング20位戦果 */
    private final XYChart.Series<Number, Number> rank20Series = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rank20Data = FXCollections.observableArrayList();
    private final BooleanProperty rank20Enabled = new SimpleBooleanProperty();

    /** ランキング100位戦果 */
    private final XYChart.Series<Number, Number> rank100Series = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rank100Data = FXCollections.observableArrayList();
    private final BooleanProperty rank100Enabled = new SimpleBooleanProperty();

    /** ランキング500位戦果 */
    private final XYChart.Series<Number, Number> rank500Series = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rank500Data = FXCollections.observableArrayList();
    private final BooleanProperty rank500Enabled = new SimpleBooleanProperty();

    /** 自分の戦果 */
    private final XYChart.Series<Number, Number> rateSeries = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rateData = FXCollections.observableArrayList();
    private final BooleanProperty rateEnabled = new SimpleBooleanProperty();

    /** 自分の順位 */
    private final XYChart.Series<Number, Number> rankNoSeries = new XYChart.Series<>();
    private final ObservableList<XYChart.Data<Number, Number>> rankNoData = FXCollections.observableArrayList();
    private final BooleanProperty rankNoEnabled = new SimpleBooleanProperty();

    /** 基準となるエポック秒 */
    private long fromEpoch;

    public RankingSeries() {
        this("");
    }

    public RankingSeries(String suffix) {
        setSeriesNameSuffix(suffix);

        rank1Series.setData(rank1Data);
        rank5Series.setData(rank5Data);
        rank20Series.setData(rank20Data);
        rank100Series.setData(rank100Data);
        rank500Series.setData(rank500Data);
        rateSeries.setData(rateData);
        rankNoSeries.setData(rankNoData);
    }

    public void setSeriesNameSuffix(String suffix) {
        rank1Series.setName("1位" + suffix);
        rank5Series.setName("5位" + suffix);
        rank20Series.setName("20位" + suffix);
        rank100Series.setName("100位" + suffix);
        rank500Series.setName("500位" + suffix);
        rateSeries.setName("自分" + suffix);
        rankNoSeries.setName("順位" + suffix);
    }

    public BooleanProperty rank1EnabledProperty() {
        return rank1Enabled;
    }

    public BooleanProperty rank5EnabledProperty() {
        return rank5Enabled;
    }

    public BooleanProperty rank20EnabledProperty() {
        return rank20Enabled;
    }

    public BooleanProperty rank100EnabledProperty() {
        return rank100Enabled;
    }

    public BooleanProperty rank500EnabledProperty() {
        return rank500Enabled;
    }

    public BooleanProperty rateEnabledProperty() {
        return rateEnabled;
    }

    public BooleanProperty rankNoEnabledProperty() {
        return rankNoEnabled;
    }

    /**
     * 基準となる日付から内部的な基準値のエポック秒を設定する
     * @param from 基準となる日付
     */
    public void setFrom(ZonedDateTime from) {
        fromEpoch = from.toEpochSecond();
    }

    /**
     * データを消去する
     */
    public void clear() {
        rank1Data.clear();
        rank5Data.clear();
        rank20Data.clear();
        rank100Data.clear();
        rank500Data.clear();
        rateData.clear();
        rankNoData.clear();
    }

    /**
     * Seriesの可視性を更新する
     */
    public void updateVisibilities() {
        if (rank1Series.getNode() != null) {
            rank1Series.getNode().setVisible(rank1Enabled.get());
        }
        if (rank5Series.getNode() != null) {
            rank5Series.getNode().setVisible(rank5Enabled.get());
        }
        if (rank20Series.getNode() != null) {
            rank20Series.getNode().setVisible(rank20Enabled.get());
        }
        if (rank100Series.getNode() != null) {
            rank100Series.getNode().setVisible(rank100Enabled.get());
        }
        if (rank500Series.getNode() != null) {
            rank500Series.getNode().setVisible(rank500Enabled.get());
        }
        if (rateSeries.getNode() != null) {
            rateSeries.getNode().setVisible(rateEnabled.get());
        }
        if (rankNoSeries.getNode() != null) {
            rankNoSeries.getNode().setVisible(rankNoEnabled.get());
        }
    }

    /**
     * データを追加する
     * @param item ランキングログ
     */
    public void add(LogItem item) {
        var delta = item.getDateTime().toEpochSecond() - fromEpoch;
        Integer value;

        value = item.getRank1();
        if (value != null && rank1Enabled.get()) {
            rank1Data.add(new XYChart.Data<>(delta, value));
        }

        value = item.getRank5();
        if (value != null && rank5Enabled.get()) {
            rank5Data.add(new XYChart.Data<>(delta, value));
        }

        value = item.getRank20();
        if (value != null && rank20Enabled.get()) {
            rank20Data.add(new XYChart.Data<>(delta, value));
        }

        value = item.getRank100();
        if (value != null && rank100Enabled.get()) {
            rank100Data.add(new XYChart.Data<>(delta, value));
        }

        value = item.getRank500();
        if (value != null && rank500Enabled.get()) {
            rank500Data.add(new XYChart.Data<>(delta, value));
        }

        value = item.getRate();
        if (value != null && rateEnabled.get()) {
            rateData.add(new XYChart.Data<>(delta, value));
        }

        value = item.getRankNo();
        if (value != null && rankNoEnabled.get()) {
            rankNoData.add(new XYChart.Data<>(delta, value));
        }
    }

    /**
     * 戦果チャートに与えるデータを取得する
     * @return LineChartに与えるデータ
     */
    @SuppressWarnings("unchecked")
    public ObservableList<XYChart.Series<Number, Number>> rankingSeriesObservable() {
        return FXCollections.observableArrayList(
                rank1Series, rank5Series, rank20Series, rank100Series, rank500Series, rateSeries);
    }

    /**
     * 順位チャートに与えるデータを取得する
     * @return LineChartに与えるデータ
     */
    @SuppressWarnings("unchecked")
    public ObservableList<XYChart.Series<Number, Number>> myRankSeriesObservable() {
        return FXCollections.observableArrayList(rankNoSeries);
    }
}
