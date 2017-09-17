package plugins.rankingchart.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import logbook.bean.AppConfig;
import logbook.bean.WindowLocation;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.rankingchart.bean.RankingChartSeries;
import plugins.rankingchart.bean.RankingLogItem;
import plugins.rankingchart.bean.RankingTableRow;
import plugins.rankingchart.util.DateTimeUtil;
import plugins.rankingchart.util.RankingDataManager;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
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
        dateCol.setComparator(new DateTimeColComparator());

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
        content.putString(String.join("\t", Arrays.asList(
                item.getDate(),
                item.getRank1().replace(",", ""),
                item.getRank5().replace(",", ""),
                item.getRank20().replace(",", ""),
                item.getRank100().replace(",", ""),
                item.getRank500().replace(",", ""),
                item.getRate().replace(",", ""),
                item.getRankNo().replace(",", "")
        )));
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    void showConfig(@SuppressWarnings("unused") ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(PluginContainer.getInstance().getClassLoader()
                    .getResource("plugins/rankingchart/gui/ranking_chart_config.fxml"));
            loader.setClassLoader(this.getClass().getClassLoader());
            Stage stage = new Stage();
            Parent root = loader.load();
            stage.setScene(new Scene(root));

            WindowController controller = loader.getController();
            controller.setWindow(stage);

            stage.initOwner(getWindow().getOwner());
            stage.setTitle("戦果チャート設定");
            stage.setOnCloseRequest(event1 -> {
                if (!event1.isConsumed()) {
                    AppConfig.get()
                            .getWindowLocationMap()
                            .put(controller.getClass().getCanonicalName(), controller.getWindowLocation());
                }
            });
            WindowLocation location = AppConfig.get()
                    .getWindowLocationMap()
                    .get(controller.getClass().getCanonicalName());
            if (location != null) {
                controller.setWindowLocation(location);
            }
            stage.show();
        } catch (Exception ex) {
            LoggerHolder.LOG.error("設定の初期化に失敗しました", ex);
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

    /**
     * 日付列ソート用Comparator
     */
    private static class DateTimeColComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            ZonedDateTime dt1 = DateTimeUtil.dateTimeFromString(o1);
            ZonedDateTime dt2 = DateTimeUtil.dateTimeFromString(o2);
            return dt1.compareTo(dt2);
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingChartMenu.class);
    }
}
