package plugins.rankingchart.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import logbook.bean.AppConfig;
import logbook.bean.WindowLocation;
import logbook.internal.gui.WindowController;
import logbook.plugin.PluginContainer;
import logbook.plugin.gui.MainExtMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RankingChartMenu implements MainExtMenu {
    @Override
    public MenuItem getContent() {
        MenuItem item = new MenuItem("戦果チャート");
        item.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(PluginContainer.getInstance().getClassLoader()
                        .getResource("plugins/rankingchart/gui/rankingchart.fxml"));
                loader.setClassLoader(this.getClass().getClassLoader());
                Stage stage = new Stage();
                Parent root = loader.load();
                stage.setScene(new Scene(root));

                WindowController controller = loader.getController();
                controller.setWindow(stage);

                stage.initOwner(((MenuItem) e.getSource()).getParentPopup().getOwnerWindow());
                stage.setTitle("戦果チャート");
                stage.setOnCloseRequest(event -> {
                    if (!event.isConsumed()) {
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
