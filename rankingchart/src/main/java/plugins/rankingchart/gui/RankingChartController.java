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
import plugins.rankingchart.bean.LogItem;
import plugins.rankingchart.bean.Period;
import plugins.rankingchart.bean.RankingSeries;
import plugins.rankingchart.util.Database;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.util.StageUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class RankingChartController extends WindowController {
    /** チャート表示用データ */
    private RankingSeries series = new RankingSeries();
    private RankingSeries series1 = new RankingSeries(" (今月)");
    private RankingSeries series2 = new RankingSeries();

    /** テーブル表示用データ */
    private ObservableList<LogItem> rows = FXCollections.observableArrayList();

    /** 期間 */
    @FXML
    private ChoiceBox<Period> periodChoice;

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
    private TableView<LogItem> table;

    /** 日付列 */
    @FXML
    private TableColumn<LogItem, ZonedDateTime> dateCol;

    /** ランキング1位戦果列 */
    @FXML
    private TableColumn<LogItem, Number> rank1Col;

    /** ランキング5位戦果列 */
    @FXML
    private TableColumn<LogItem, Number> rank5Col;

    /** ランキング20位戦果列 */
    @FXML
    private TableColumn<LogItem, Number> rank20Col;

    /** ランキング100位戦果列 */
    @FXML
    private TableColumn<LogItem, Number> rank100Col;

    /** ランキング500位戦果列 */
    @FXML
    private TableColumn<LogItem, Number> rank500Col;

    /** 自分の戦果列 */
    @FXML
    private TableColumn<LogItem, Number> rateCol;

    /** 自分の順位列 */
    @FXML
    private TableColumn<LogItem, Number> rankNoCol;

    @FXML
    void initialize() {
        // チャートを初期化
        chart.setData(series.rankingSeriesObservable());
        ObservableList<XYChart.Series<Number, Number>> momData = FXCollections.observableArrayList();
        momData.addAll(series2.rankingSeriesObservable());
        momData.addAll(series1.rankingSeriesObservable());
        chart2.setData(momData);

        // チャートの表示項目をチェックボックスにバインド
        bindSeries(series);
        bindSeries(series1);
        bindSeries(series2);

        // テーブルセルのファクトリをセット
        dateCol.setCellFactory(new DateTimeFormatCell.Factory());
        NumberCellFactory numberFormatCellFactory = new NumberFormatCell.Factory();
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

        // 軸を初期化
        xAxis.setTickUnit(24 * 60 * 60);
        xAxis.setForceZeroInRange(true);
        yAxis.setForceZeroInRange(true);

        xAxis2.setTickUnit(24 * 60 * 60);
        xAxis2.setTickLabelFormatter(new ElapsedDaysStringConverter());
        xAxis2.setForceZeroInRange(true);
        yAxis2.setForceZeroInRange(true);

        // 表示期間を初期化
        List<Period> periods = rankingPeriods();
        periodChoice.setItems(FXCollections.observableList(periods));
        switch (periods.size()) {
            case 0:
                break;
            case 1:
                periodChoice.setValue(periods.get(0));
                break;
            default:
                // index=0,1,2はそれぞれYoY,QoQ,MoM
                periodChoice.setValue(periods.get(3));
                break;
        }
    }

    private void bindSeries(RankingSeries series) {
        series.rank1EnabledProperty().bind(rank1Check.selectedProperty());
        series.rank5EnabledProperty().bind(rank5Check.selectedProperty());
        series.rank20EnabledProperty().bind(rank20Check.selectedProperty());
        series.rank100EnabledProperty().bind(rank100Check.selectedProperty());
        series.rank500EnabledProperty().bind(rank500Check.selectedProperty());
        series.rateEnabledProperty().bind(rateCheck.selectedProperty());
    }

    @FXML
    void change() {
        Period period = periodChoice.getValue();
        if (period != null) {
            if (period.getOver() != null) {
                updateMoMRankingChart(period.getOver());
            } else {
                updateRankingChart(period.getFrom(), period.getTo());
            }
        }
    }

    private void updateRankingChart(ZonedDateTime from, ZonedDateTime to) {
        chart.setVisible(true);
        chart2.setVisible(false);

        series.clear();
        rows.clear();

        xAxis.setTickLabelFormatter(new DateStringConverter(from));

        series.setFrom(from);

        addAllItems(series, Database.getDefault().load(from, to));
    }

    private void updateMoMRankingChart(Period.Over over) {
        chart.setVisible(false);
        chart2.setVisible(true);

        series1.clear();
        series2.clear();
        rows.clear();

        ZonedDateTime today, from1, from2, to1, to2;
        today = DateTimeUtil.now().truncatedTo(ChronoUnit.DAYS);
        from1 = today.with(TemporalAdjusters.firstDayOfMonth());
        switch (over) {
            case YEAR:
                from2 = from1.minusYears(1);
                break;
            case QUARTER:
                from2 = from1.minusMonths(3);
                break;
            case MONTH:
                from2 = from1.minusMonths(1);
                break;
            default:
                from2 = from1;
        }
        to1 = from1.with(TemporalAdjusters.lastDayOfMonth()).withHour(23);
        to2 = from2.with(TemporalAdjusters.lastDayOfMonth()).withHour(23);

        series1.setFrom(from1);
        series2.setFrom(from2);
        series2.setOver(over);

        addAllItems(series1, Database.getDefault().load(from1, to1));
        addAllItems(series2, Database.getDefault().load(from2, to2));
    }

    private void addAllItems(RankingSeries series, List<LogItem> items) {
        rows.addAll(items);

        ListIterator<LogItem> li = items.listIterator(items.size());
        while (li.hasPrevious()) {
            series.add(li.previous());
        }
    }

    @FXML
    void copy() {
        LogItem item = table.getSelectionModel().getSelectedItem();
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
                .sorted(Comparator.comparing(LogItem::getDateTime))
                .forEach(item -> {
                    // ラムダ式の中でIOExceptionをハンドリングしたくないし、
                    // 月次ログ程度では大してメモリを使わないのでFileWriterを使わずに
                    // いったんStringBufferにCSVを書き出している。
                    sb.append(item.toCSV());
                    sb.append("\r\n");
                });

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(LogItem.csvHeader());
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

    private List<Period> rankingPeriods() {
        final TemporalAdjuster firstDayOfMonthAdjuster = TemporalAdjusters.firstDayOfMonth();
        final TemporalAdjuster lastDayOfMonthAdjuster = TemporalAdjusters.lastDayOfMonth();

        List<Period> periods = Database.getDefault()
                .loadAll()
                .stream()
                .map(LogItem::getDateTime)
                .map(dt -> dt.with(firstDayOfMonthAdjuster).truncatedTo(ChronoUnit.DAYS))
                .distinct()
                .map(dt -> new Period(DateTimeUtil.formatMonth(dt),
                        dt, dt.with(lastDayOfMonthAdjuster).withHour(23)))
                .collect(Collectors.toList());

        if (periods.size() >= 2) {
            // 2ヶ月分以上のデータがある場合は比較モードを有効にする
            periods.add(0, new Period(Period.Over.YEAR));
            periods.add(1, new Period(Period.Over.QUARTER));
            periods.add(2, new Period(Period.Over.MONTH));
        }

        return periods;
    }

    private static abstract class NumberToStringConverter extends StringConverter<Number> {
        @Override
        public Number fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * チャートのX軸(日付)ラベル用コンバーター
     */
    private static class DateStringConverter extends NumberToStringConverter {
        private final long epoch;

        DateStringConverter(ZonedDateTime from) {
            epoch = from.toEpochSecond();
        }

        @Override
        public String toString(Number delta) {
            return DateTimeUtil.formatDate(DateTimeUtil.dateTimeFromEpoch(epoch + delta.longValue()));
        }
    }

    /**
     * チャートのX軸(日数)ラベル用コンバーター
     */
    private static class ElapsedDaysStringConverter extends NumberToStringConverter {
        @Override
        public String toString(Number delta) {
            long seconds = delta.longValue();
            String ampm = (seconds % 86400 < 43200) ? "AM" : "PM";
            return String.format("%d日%s", seconds / 86400 + 1, ampm);
        }
    }

    private interface DateTimeCellFactory extends Callback<TableColumn<LogItem, ZonedDateTime>, TableCell<LogItem, ZonedDateTime>> {
    }

    private interface NumberCellFactory extends Callback<TableColumn<LogItem, Number>, TableCell<LogItem, Number>> {
    }

    /**
     * 日付列セル
     */
    private static class DateTimeFormatCell extends TableCell<LogItem, ZonedDateTime> {
        @Override
        protected void updateItem(ZonedDateTime item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : DateTimeUtil.formatDateTime(item));
        }

        static class Factory implements DateTimeCellFactory {
            @Override
            public TableCell<LogItem, ZonedDateTime> call(TableColumn<LogItem, ZonedDateTime> param) {
                return new DateTimeFormatCell();
            }
        }
    }

    /**
     * 数値列セル
     */
    private static class NumberFormatCell extends TableCell<LogItem, Number> {
        @Override
        protected void updateItem(Number item, boolean empty) {
            super.updateItem(item, empty);
            setText(item == null ? "" : NumberFormat.getNumberInstance().format(item));
        }

        static class Factory implements NumberCellFactory {
            @Override
            public TableCell<LogItem, Number> call(TableColumn<LogItem, Number> param) {
                return new NumberFormatCell();
            }
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingChartMenu.class);
    }
}
