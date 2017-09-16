package plugins.rankingchart.bean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class RankingChartSeries {
    /** ランキング1位戦果 */
    private XYChart.Series<Number, Number> rank1Series = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rank1Data = FXCollections.observableArrayList();
    private BooleanProperty rank1Enabled = new SimpleBooleanProperty();

    /** ランキング5位戦果 */
    private XYChart.Series<Number, Number> rank5Series = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rank5Data = FXCollections.observableArrayList();
    private BooleanProperty rank5Enabled = new SimpleBooleanProperty();

    /** ランキング20位戦果 */
    private XYChart.Series<Number, Number> rank20Series = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rank20Data = FXCollections.observableArrayList();
    private BooleanProperty rank20Enabled = new SimpleBooleanProperty();

    /** ランキング100位戦果 */
    private XYChart.Series<Number, Number> rank100Series = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rank100Data = FXCollections.observableArrayList();
    private BooleanProperty rank100Enabled = new SimpleBooleanProperty();

    /** ランキング500位戦果 */
    private XYChart.Series<Number, Number> rank500Series = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rank500Data = FXCollections.observableArrayList();
    private BooleanProperty rank500Enabled = new SimpleBooleanProperty();

    /** 自分の戦果 */
    private XYChart.Series<Number, Number> rateSeries = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rateData = FXCollections.observableArrayList();
    private BooleanProperty rateEnabled = new SimpleBooleanProperty();

    /** 自分の順位 */
    private XYChart.Series<Number, Number> rankNoSeries = new XYChart.Series<>();
    private ObservableList<XYChart.Data<Number, Number>> rankNoData = FXCollections.observableArrayList();
    private BooleanProperty rankNoEnabled = new SimpleBooleanProperty();

    /** 全データ */
    private List<ObservableList<XYChart.Data<Number, Number>>> allData;

    /** 基準となるエポック秒 */
    private long fromEpoch;

    public RankingChartSeries() {
        rank1Series.setName("1位");
        rank1Series.setData(rank1Data);

        rank5Series.setName("5位");
        rank5Series.setData(rank5Data);

        rank20Series.setName("20位");
        rank20Series.setData(rank20Data);

        rank100Series.setName("100位");
        rank100Series.setData(rank100Data);

        rank500Series.setName("500位");
        rank500Series.setData(rank500Data);

        rateSeries.setName("自分");
        rateSeries.setData(rateData);

        rankNoSeries.setName("順位");
        rankNoSeries.setData(rankNoData);

        allData = Arrays.asList(rank1Data, rank5Data, rank20Data, rank100Data, rank500Data, rateData, rankNoData);
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
        allData.forEach(ObservableList::clear);
    }

    /**
     * データを追加する
     * @param item ランキングログ
     */
    public void add(RankingLogItem item) {
        Long delta = item.getDateTime().toEpochSecond() - fromEpoch;
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
