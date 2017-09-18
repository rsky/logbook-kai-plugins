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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import logbook.internal.gui.WindowController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingChartSeries;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.bean.RankingTableRow;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.rankingchart.util.RankingDataManager;
import plugins.util.StageUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    /** テーブル */
    @FXML
    private TableView<RankingTableRow> table;

    /** 日付列 */
    @FXML
    private TableColumn<RankingTableRow, String> dateCol;

    /** ランキング1位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank1Col;

    /** ランキング5位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank5Col;

    /** ランキング20位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank20Col;

    /** ランキング100位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank100Col;

    /** ランキング500位戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rank500Col;

    /** 自分の戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rateCol;

    /** 自分の順位列 */
    @FXML
    private TableColumn<RankingTableRow, String> rankNoCol;

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
        dateCol.setComparator((o1, o2) -> {
            ZonedDateTime dt1 = DateTimeUtil.dateTimeFromString(o1);
            ZonedDateTime dt2 = DateTimeUtil.dateTimeFromString(o2);
            return dt1.compareTo(dt2);
        });

        // テーブルのセルをデータのプロパティにバインド
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        rank1Col.setCellValueFactory(new PropertyValueFactory<>("rank1"));
        rank5Col.setCellValueFactory(new PropertyValueFactory<>("rank5"));
        rank20Col.setCellValueFactory(new PropertyValueFactory<>("rank20"));
        rank100Col.setCellValueFactory(new PropertyValueFactory<>("rank100"));
        rank500Col.setCellValueFactory(new PropertyValueFactory<>("rank500"));
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));
        rankNoCol.setCellValueFactory(new PropertyValueFactory<>("rankNo"));

        // 表示期間を初期化
        ObservableList<RankingPeriod> periods = rankingPeriodsObservable();
        periodChoice.setItems(periods);
        if (periods.size() > 0) {
            periodChoice.setValue(periods.get(0));
        }
    }

    @FXML
    void change(@SuppressWarnings("unused") ActionEvent event) {
        final RankingPeriod period = periodChoice.getValue();
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


    @FXML
    void copy(@SuppressWarnings("unused") ActionEvent event) {
        RankingTableRow item = table.getSelectionModel().getSelectedItem();
        ClipboardContent content = new ClipboardContent();
        content.putString(item.toTSV());
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    void save(@SuppressWarnings("unused") ActionEvent event) {
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

        table.getItems().sorted((r1, r2) -> {
            ZonedDateTime dt1 = DateTimeUtil.dateTimeFromString(r1.getDate());
            ZonedDateTime dt2 = DateTimeUtil.dateTimeFromString(r2.getDate());
            return dt1.compareTo(dt2);
        }).forEach(row -> {
            // ラムダ式の中でIOExceptionをハンドリングしたくないし、
            // そこまでメモリを使うわけでもないのでFileWriterを使わずに
            // いったんStringBufferにCSVを書き出している。
            sb.append(row.toCSV());
            sb.append("\r\n");
        });

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("日付,1位,5位,20位,100位,500位,戦果,順位\r\n");
            writer.write(sb.toString());
        } catch (IOException e) {
            LoggerHolder.LOG.error("CSVを保存できませんでした", e);
        }
    }

    @FXML
    void showConfig(@SuppressWarnings("unused") ActionEvent event) {
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

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingChartMenu.class);
    }
}
