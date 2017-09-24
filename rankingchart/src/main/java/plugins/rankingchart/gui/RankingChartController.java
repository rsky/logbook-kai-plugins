package plugins.rankingchart.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import logbook.internal.gui.WindowController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingChartSeries;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.rankingchart.util.RankingDataManager;
import plugins.util.StageUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class RankingChartController extends WindowController {
    /** チャート表示用データ */
    private RankingChartSeries series = new RankingChartSeries();
    private RankingChartSeries series1 = new RankingChartSeries(" (今月)");
    private RankingChartSeries series2 = new RankingChartSeries(" (前月)");

    /** テーブル表示用データ */
    private ObservableList<RankingLogItem> rows = FXCollections.observableArrayList();

    /** 期間 */
    @FXML
    private ChoiceBox<RankingPeriod> periodChoice;

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

    /** MoM戦果チャート */
    @FXML
    private AreaChart<Number, Number> chart2;

    /** MoM戦果チャートX軸 */
    @FXML
    private NumberAxis xAxis2;

    /** MoM戦果チャートY軸 */
    @FXML
    private NumberAxis yAxis2;

    /** テーブル */
    @FXML
    private TableView<RankingLogItem> table;

    /** 日付列 */
    @FXML
    private TableColumn<RankingLogItem, ZonedDateTime> dateCol;

    /** ランキング1位戦果列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rank1Col;

    /** ランキング5位戦果列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rank5Col;

    /** ランキング20位戦果列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rank20Col;

    /** ランキング100位戦果列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rank100Col;

    /** ランキング500位戦果列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rank500Col;

    /** 自分の戦果列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rateCol;

    /** 自分の順位列 */
    @FXML
    private TableColumn<RankingLogItem, Number> rankNoCol;

    @FXML
    void initialize() {
        // チャートを初期化
        chart.setData(series.rankingSeriesObservable());
        ObservableList<XYChart.Series<Number, Number>> momData = series2.rankingSeriesObservable();
        momData.addAll(series1.rankingSeriesObservable());
        chart2.setData(momData);

        // チャートの表示項目をチェックボックスにバインド
        bindSeries(series);
        bindSeries(series1);
        bindSeries(series2);

        // テーブルセルのファクトリをセット
        dateCol.setCellFactory(new DateTimeFormatCellFactory());
        NumberFormatCellFactory numberFormatCellFactory = new NumberFormatCellFactory();
        rank1Col.setCellFactory(numberFormatCellFactory);
        rank5Col.setCellFactory(numberFormatCellFactory);
        rank20Col.setCellFactory(numberFormatCellFactory);
        rank100Col.setCellFactory(numberFormatCellFactory);
        rank500Col.setCellFactory(numberFormatCellFactory);
        rateCol.setCellFactory(numberFormatCellFactory);
        rankNoCol.setCellFactory(numberFormatCellFactory);

        // テーブルのセルをデータのプロパティにバインド
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        rank1Col.setCellValueFactory(new PropertyValueFactory<>("rank1"));
        rank5Col.setCellValueFactory(new PropertyValueFactory<>("rank5"));
        rank20Col.setCellValueFactory(new PropertyValueFactory<>("rank20"));
        rank100Col.setCellValueFactory(new PropertyValueFactory<>("rank100"));
        rank500Col.setCellValueFactory(new PropertyValueFactory<>("rank500"));
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));
        rankNoCol.setCellValueFactory(new PropertyValueFactory<>("rankNo"));

        // テーブルを初期化
        table.setItems(rows);

        // 表示期間を初期化
        ObservableList<RankingPeriod> periods = rankingPeriodsObservable();
        periodChoice.setItems(periods);
        switch (periods.size()) {
            case 0:
                break;
            case 1:
                periodChoice.setValue(periods.get(0));
                break;
            default:
                // index=0はMoM
                periodChoice.setValue(periods.get(1));
                break;
        }
    }

    private void bindSeries(RankingChartSeries series) {
        series.rank1EnabledProperty().bind(rank1Check.selectedProperty());
        series.rank5EnabledProperty().bind(rank5Check.selectedProperty());
        series.rank20EnabledProperty().bind(rank20Check.selectedProperty());
        series.rank100EnabledProperty().bind(rank100Check.selectedProperty());
        series.rank500EnabledProperty().bind(rank500Check.selectedProperty());
        series.rateEnabledProperty().bind(rateCheck.selectedProperty());
    }

    @FXML
    void change() {
        final RankingPeriod period = periodChoice.getValue();
        List<RankingLogItem> items = null;

        if (period != null) {
            if (period.getFrom() == null) {
                showMoM();
                return;
            }
            items = RankingDataManager.getDefault().load(period.getFrom(), period.getTo());
        }

        chart.setVisible(true);
        chart2.setVisible(false);

        series.clear();
        rows.clear();

        if (items != null && items.size() > 0) {
            // 0時を基準とし、大目盛は1日単位
            ZonedDateTime baseTime = period.getFrom().withHour(0);
            xAxis.setTickUnit(24 * 60 * 60);
            xAxis.setTickLabelFormatter(new TimeDeltaStringConverter(baseTime));
            // 常にゼロを基準
            xAxis.setForceZeroInRange(true);
            yAxis.setForceZeroInRange(true);

            series.setFrom(baseTime);

            rows.addAll(items);

            Collections.reverse(items);
            items.forEach(series::add);
        }
    }

    private void showMoM() {
        chart.setVisible(false);
        chart2.setVisible(true);

        series1.clear();
        series2.clear();
        rows.clear();

        xAxis2.setTickUnit(24 * 60 * 60);
        xAxis2.setTickLabelFormatter(new TimeDeltaStringConverter());
        xAxis2.setForceZeroInRange(true);
        yAxis2.setForceZeroInRange(true);

        RankingPeriod period1 = periodChoice.getItems().get(1);
        RankingPeriod period2 = periodChoice.getItems().get(2);

        series1.setFrom(period1.getFrom().withDayOfMonth(1).withHour(0));
        series2.setFrom(period2.getFrom().withDayOfMonth(1).withHour(0));

        List<RankingLogItem> items1 = RankingDataManager.getDefault().load(period1.getFrom(), period1.getTo());
        List<RankingLogItem> items2 = RankingDataManager.getDefault().load(period2.getFrom(), period2.getTo());

        rows.addAll(items1);
        rows.addAll(items2);

        Collections.reverse(items1);
        items1.forEach(series1::add);

        Collections.reverse(items2);
        items2.forEach(series2::add);
    }

    @FXML
    void copy() {
        RankingLogItem item = table.getSelectionModel().getSelectedItem();
        ClipboardContent content = new ClipboardContent();
        content.putString(item.toTSV());
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    void save() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        chooser.setInitialFileName(periodChoice.getSelectionModel().getSelectedItem().getName());

        File file = chooser.showSaveDialog(getWindow());
        if (file != null){
            saveCSV(file);
        }
    }

    private void saveCSV(File file) {
        StringBuffer sb = new StringBuffer();

        table.getItems()
                .sorted(Comparator.comparing(RankingLogItem::getDateTime))
                .forEach(item -> {
                    // ラムダ式の中でIOExceptionをハンドリングしたくないし、
                    // 月次ログ程度では大してメモリを使わないのでFileWriterを使わずに
                    // いったんStringBufferにCSVを書き出している。
                    sb.append(item.toCSV());
                    sb.append("\r\n");
                });

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(RankingLogItem.csvHeader());
            writer.write("\r\n");
            writer.write(sb.toString());
        } catch (IOException e) {
            LoggerHolder.LOG.error("CSVを保存できませんでした", e);
        }
    }

    @FXML
    void showConfig() {
        try {
            StageUtil.show(
                    "戦果チャート設定",
                    "plugins/rankingchart/gui/ranking_chart_config.fxml",
                    getWindow().getOwner(),
                    getClass().getClassLoader()
            );
        } catch (Exception e) {
            LoggerHolder.LOG.error("設定の初期化に失敗しました", e);
        }
    }

    private ObservableList<RankingPeriod> rankingPeriodsObservable() {
        LinkedHashMap<String, RankingPeriod> map = new LinkedHashMap<>();
        List<RankingLogItem> allItems = RankingDataManager.getDefault().loadAll();

        for (RankingLogItem item : allItems) {
            String monthStr = DateTimeUtil.formatMonth(item.getDateTime());
            RankingPeriod period = map.get(monthStr);
            if (period != null) {
                period.extend(item);
            } else {
                map.put(monthStr, new RankingPeriod(monthStr, item));
            }
        }

        ObservableList<RankingPeriod> periods = FXCollections.observableArrayList(map.values());
        if (periods.size() >= 2) {
            // 2ヶ月分以上のデータがある場合はMonth-over-Monthモードを有効にする
            periods.add(0, new RankingPeriod("前月比"));
        }
        return periods;
    }

    /**
     * ランキング期間
     */
    private static class RankingPeriod {
        final private String name;
        private ZonedDateTime from;
        private ZonedDateTime to;

        RankingPeriod(String name, RankingLogItem item) {
            this(name);
            from = to = item.getDateTime();
        }

        RankingPeriod(String name) {
            this.name = name;
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

        String getName() {
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

        TimeDeltaStringConverter() {
            epoch = 0;
        }

        TimeDeltaStringConverter(ZonedDateTime from) {
            epoch = from.toEpochSecond();
        }

        @Override
        public String toString(Number delta) {
            long seconds = delta.longValue();
            String ampm = (seconds % 86400 < 43200) ? "AM" : "PM";
            if (epoch == 0) {
                return String.format("%d日%s", seconds / 86400 + 1, ampm);
            } else {
                return DateTimeUtil.formatDate(DateTimeUtil.dateTimeFromEpoch(epoch + delta.longValue()));
            }
        }

        @Override
        public Number fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }

    private static class DateTimeFormatCell extends TableCell<RankingLogItem, ZonedDateTime> {
        @Override
        protected void updateItem(ZonedDateTime item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : DateTimeUtil.formatDateTime(item));
        }
    }

    private static class DateTimeFormatCellFactory implements Callback<TableColumn<RankingLogItem, ZonedDateTime>, TableCell<RankingLogItem, ZonedDateTime>> {
        @Override
        public TableCell<RankingLogItem, ZonedDateTime> call(TableColumn<RankingLogItem, ZonedDateTime> param) {
            return new DateTimeFormatCell();
        }
    }

    private static class NumberFormatCell extends TableCell<RankingLogItem, Number> {
        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : NumberFormat.getNumberInstance().format(item));
        }
    }

    private static class NumberFormatCellFactory implements Callback<TableColumn<RankingLogItem, Number>, TableCell<RankingLogItem, Number>> {
        @Override
        public TableCell<RankingLogItem, Number> call(TableColumn<RankingLogItem, Number> param) {
            return new NumberFormatCell();
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingChartMenu.class);
    }
}
