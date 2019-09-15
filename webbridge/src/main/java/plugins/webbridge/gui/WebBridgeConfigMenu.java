package plugins.webbridge.gui;

import javafx.scene.control.MenuItem;
import logbook.plugin.gui.MainExtMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.util.StageUtil;

public class WebBridgeConfigMenu implements MainExtMenu {
    @Override
    public MenuItem getContent() {
        MenuItem item = new MenuItem("API Web Bridge");
        item.setOnAction(event -> {
            try {
                StageUtil.show(
                        "API Web Bridgeの設定",
                        "plugins/webbridge/gui/webbridge_config.fxml",
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
        private static final Logger LOG = LogManager.getLogger(WebBridgeConfigMenu.class);
    }
}
