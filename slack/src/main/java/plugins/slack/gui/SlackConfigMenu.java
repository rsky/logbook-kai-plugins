package plugins.slack.gui;

import javafx.scene.control.MenuItem;
import logbook.plugin.gui.MainExtMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.util.StageUtil;

public class SlackConfigMenu implements MainExtMenu {
    @Override
    public MenuItem getContent() {
        MenuItem item = new MenuItem("Slack");
        item.setOnAction(event -> {
            try {
                StageUtil.show(
                        "Slackの設定",
                        "plugins/Slack/gui/Slack_config.fxml",
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
        private static final Logger LOG = LogManager.getLogger(SlackConfigMenu.class);
    }
}
