package plugins.discord.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.internal.gui.WindowController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.discord.api.Pusher;
import plugins.discord.bean.*;

public class DiscordConfigController extends WindowController {

    /**
     * Incoming Webhook URL
     */
    @FXML
    private TextField incomingWebhookUrl;

    /**
     * 遠征完了を通知する
     */
    @FXML
    private CheckBox notifyMissionCompleted;

    /**
     * 入渠完了を通知する
     */
    @FXML
    private CheckBox notifyNdockCompleted;

    /**
     * 初期化
     */
    @SuppressWarnings("unused")
    @FXML
    void initialize() {
        var config = DiscordConfig.get();
        incomingWebhookUrl.setText(config.getIncomingWebhookUrl());
        notifyMissionCompleted.setSelected(config.isNotifyMissionCompleted());
        notifyNdockCompleted.setSelected(config.isNotifyNdockCompleted());
    }

    /**
     * キャンセル
     */
    @FXML
    void cancel() {
        getWindow().close();
    }

    /**
     * 設定の反映
     */
    @FXML
    void ok() {
        var config = DiscordConfig.get();
        config.setIncomingWebhookUrl(incomingWebhookUrl.getText());
        config.setNotifyMissionCompleted(notifyMissionCompleted.isSelected());
        config.setNotifyNdockCompleted(notifyNdockCompleted.isSelected());

        ThreadManager.getExecutorService()
                .execute(Config.getDefault()::store);
        getWindow().close();
    }

    /**
     * テスト通知
     */
    @FXML
    void test() {
        new Pusher(incomingWebhookUrl.getText()).push(
                "送信テスト",
                "航海日誌 Discord Plugin より",
                pushes -> Platform.runLater(() ->
                        showAlert(Alert.AlertType.INFORMATION, "テスト通知を送信しました")
                ),
                throwable -> {
                    LoggerHolder.logError(throwable);
                    Platform.runLater(() ->
                            showAlert(Alert.AlertType.ERROR, "テスト通知の送信に失敗しました")
                    );
                });
    }

    private void showAlert(Alert.AlertType alertType, String contentText) {
        var alert = new Alert(alertType, contentText, ButtonType.OK);
        alert.initOwner(getWindow().getOwner());
        alert.show();
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(DiscordConfigController.class);

        private static void logError(Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
