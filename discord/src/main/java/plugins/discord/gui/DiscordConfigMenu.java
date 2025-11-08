package plugins.discord.gui;

import javafx.scene.control.MenuItem;
import logbook.plugin.gui.MainExtMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.util.StageUtil;

public class DiscordConfigMenu implements MainExtMenu {
    @Override
    public MenuItem getContent() {
        var item = new MenuItem("Discord");
        item.setOnAction(event -> {
            try {
                StageUtil.show(
                        "Discordの設定",
                        "plugins/discord/gui/discord_config.fxml",
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
        private static final Logger LOG = LogManager.getLogger(DiscordConfigMenu.class);
    }
}
