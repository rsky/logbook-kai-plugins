package plugins.rankingchart.gui;

import javafx.scene.control.MenuItem;
import logbook.plugin.gui.MainExtMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.util.StageUtil;

public class RankingChartMenu implements MainExtMenu {
    @Override
    public MenuItem getContent() {
        var item = new MenuItem("戦果チャート");
        item.setOnAction(event -> {
            try {
                StageUtil.show(
                        "戦果チャート",
                        "plugins/rankingchart/gui/ranking_chart.fxml",
                        ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow(),
                        getClass().getClassLoader()
                );
            } catch (Exception e) {
                LoggerHolder.LOG.error("設定の初期化に失敗しました", e);
            }
        });
        return item;
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(RankingChartMenu.class);
    }
}
