package plugins.rankingchart.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import logbook.internal.gui.WindowController;
import plugins.rankingchart.bean.RankingChartSeries;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.bean.RankingTableRow;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.rankingchart.util.RankingDataManager;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RankingChartController extends WindowController {
    /** 全期間のデータ */
    private List<RankingLogItem> allItems;

    /** チャート表示用データ */
    private RankingChartSeries series;

    /** テーブル表示用データ */
    private ObservableList<RankingTableRow> rows;

    /** 期間 */
    @FXML
    private ChoiceBox<RankingPeriod> period;

    /** ランキング1位戦果 */
    @FXML
    private CheckBox rank1Check;

    /** ランキング5位戦果 */
    @FXML
    private CheckBox rank5Check;

    /** ランキング220位戦果 */
    @FXML
    private CheckBox rank20Check;

    /** ランキング100位戦果 */
    @FXML
    private CheckBox rank100Check;

    /** ランキング500位戦果 */
    @FXML
    private CheckBox rank500Check;

    /** 自分の戦果 */
    @FXML
    private CheckBox rateCheck;

    /** 戦果チャート */
    @FXML
    private LineChart<Number, Number> chart;

    /** 戦果チャートX軸 */
    @FXML
    private NumberAxis xAxis;

    /** 戦果チャートY軸 */
    @FXML
    private NumberAxis yAxis;

    /** テーブル */
    @FXML
    private TableView<RankingTableRow> table;

    /** 日付列 */
    @FXML
    private TableColumn<RankingTableRow, String> date;

    /** ランキング1位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank1;

    /** ランキング5位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank5;

    /** ランキング20位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank20;

    /** ランキング100位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank100;

    /** ランキング500位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank500;

    /** 自分の戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rate;

    /** 自分の順位列 */
    @FXML
    private TableColumn<RankingTableRow, String> rankNo;

    @FXML
    void initialize() {
        // データ一括読み込み
        allItems = RankingDataManager.getDefault().loadAll();

        // チャートを初期化
        series = new RankingChartSeries();
        chart.setData(series.rankingSeriesObservable());

        // チャートの表示項目をチェックボックスにバインド
        series.rank1EnabledProperty().bind(rank1Check.selectedProperty());
        series.rank5EnabledProperty().bind(rank5Check.selectedProperty());
        series.rank20EnabledProperty().bind(rank20Check.selectedProperty());
        series.rank100EnabledProperty().bind(rank100Check.selectedProperty());
        series.rank500EnabledProperty().bind(rank500Check.selectedProperty());
        series.rateEnabledProperty().bind(rateCheck.selectedProperty());

        // テーブルを初期化
        rows = FXCollections.observableArrayList();
        table.setItems(rows);

        // テーブルのセルをデータのプロパティにバインド
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        rank1.setCellValueFactory(new PropertyValueFactory<>("rank1"));
        rank5.setCellValueFactory(new PropertyValueFactory<>("rank5"));
        rank20.setCellValueFactory(new PropertyValueFactory<>("rank20"));
        rank100.setCellValueFactory(new PropertyValueFactory<>("rank100"));
        rank500.setCellValueFactory(new PropertyValueFactory<>("rank500"));
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        rankNo.setCellValueFactory(new PropertyValueFactory<>("rankNo"));

        // 表示期間を初期化
        ObservableList<RankingPeriod> periods = rankingPeriodsObservable();
        period.setItems(periods);
        if (periods.size() > 0) {
            period.setValue(periods.get(0));
        }
    }

    @FXML
    void change(@SuppressWarnings("unused") ActionEvent event) {
        final RankingPeriod period = this.period.getValue();
        List<RankingLogItem> items = null;

        if (period != null) {
            final ZonedDateTime from = period.getFrom();
            final ZonedDateTime to = period.getTo();
            items = allItems
                    .stream()
                    .filter(item -> !item.getDateTime().isBefore(from))
                    .filter(item -> !item.getDateTime().isAfter(to))
                    .collect(Collectors.toList());
        }

        series.clear();
        rows.clear();

        if (items != null && items.size() > 0) {
            // 0時を基準とし、大目盛は1日単位
            ZonedDateTime beginningOfDay = period.getFrom().withHour(0);
            xAxis.setTickUnit(24 * 60 * 60);
            xAxis.setTickLabelFormatter(new TimeDeltaStringConverter(beginningOfDay));
            // 常にゼロを基準
            xAxis.setForceZeroInRange(true);
            yAxis.setForceZeroInRange(true);

            series.setFrom(beginningOfDay);
            items.forEach(series::add);

            rows.addAll(items
                    .stream()
                    .map(RankingTableRow::new)
                    .collect(Collectors.toList())
            );
        }
    }

    private ObservableList<RankingPeriod> rankingPeriodsObservable() {
        LinkedHashMap<String, RankingPeriod> map = new LinkedHashMap<>();

        for (RankingLogItem item : allItems) {
            String monthStr = DateTimeUtil.formatMonth(item.getDateTime());
            RankingPeriod period = map.get(monthStr);
            if (period != null) {
                period.extend(item);
            } else {
                map.put(monthStr, new RankingPeriod(monthStr, item));
            }
        }

        return FXCollections.observableArrayList(map.values());
    }

    /**
     * ランキング期間
     */
    private static class RankingPeriod {
        final private String name;
        private ZonedDateTime from;
        private ZonedDateTime to;

        RankingPeriod(String name, RankingLogItem item) {
            this.name = name;
            from = to = item.getDateTime();
        }

        void extend(RankingLogItem item) {
            ZonedDateTime dt = item.getDateTime();
            if (from.isAfter(dt)) {
                from = dt;
            }
            if (to.isBefore(dt)) {
                to = dt;
            }
        }

        @Override
        public String toString() {
            return name;
        }

        ZonedDateTime getFrom() {
            return from;
        }

        ZonedDateTime getTo() {
            return to;
        }
    }

    /**
     * チャートのX軸(日付)ラベル用コンバーター
     */
    private static class TimeDeltaStringConverter extends StringConverter<Number> {
        private final long epoch;

        TimeDeltaStringConverter(ZonedDateTime from) {
            epoch = from.toEpochSecond();
        }

        @Override
        public String toString(Number delta) {
            return DateTimeUtil.formatDate(DateTimeUtil.dateTimeFromEpoch(epoch + delta.longValue()));
        }

        @Override
        public Number fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
