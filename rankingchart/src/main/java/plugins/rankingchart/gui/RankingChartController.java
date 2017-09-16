package plugins.rankingchart.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logbook.internal.gui.WindowController;
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

    /** テーブル表示用データ */
    private ObservableList<RankingTableRow> rows;

    /** 期間 */
    @FXML
    private ChoiceBox<RankingPeriod> period;

    @FXML
    private CheckBox rank1CheckBox;

    @FXML
    private CheckBox rank5CheckBox;

    @FXML
    private CheckBox rank20CheckBox;

    @FXML
    private CheckBox rank100CheckBox;

    @FXML
    private CheckBox rank500CheckBox;

    @FXML
    private CheckBox rateCheckBox;

    @FXML
    private CheckBox rankNoCheckBox;

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

    /** マイ戦果列 */
    @FXML
    private TableColumn<RankingTableRow, String> rate;

    /** マイランク列 */
    @FXML
    private TableColumn<RankingTableRow, String> rankNo;

    @FXML
    void initialize() {
        allItems = RankingDataManager.getDefault().loadAll();
        rows = FXCollections.observableArrayList();
        table.setItems(rows);

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        rank1.setCellValueFactory(new PropertyValueFactory<>("rank1"));
        rank5.setCellValueFactory(new PropertyValueFactory<>("rank5"));
        rank20.setCellValueFactory(new PropertyValueFactory<>("rank20"));
        rank100.setCellValueFactory(new PropertyValueFactory<>("rank100"));
        rank500.setCellValueFactory(new PropertyValueFactory<>("rank500"));
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        rankNo.setCellValueFactory(new PropertyValueFactory<>("rankNo"));

        ObservableList<RankingPeriod> periods = getMonthList();
        period.setItems(periods);
        if (periods.size() > 0) {
            period.setValue(periods.get(0));
        }
    }

    @FXML
    void change(ActionEvent event) {
    }

    @FXML
    void changePeriod(ActionEvent event) {
        final RankingPeriod period = this.period.getValue();
        rows.clear();
        if (period != null) {
            rows.addAll(allItems
                    .stream()
                    .filter(item -> !item.getDateTime().isBefore(period.getFrom()))
                    .filter(item -> !item.getDateTime().isAfter(period.getTo()))
                    .map(RankingTableRow::new)
                    .collect(Collectors.toList())
            );
        }
    }

    private ObservableList<RankingPeriod> getMonthList() {
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
}
