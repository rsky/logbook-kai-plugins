package plugins.slack.gui;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.internal.gui.WindowController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.slack.api.SlackService;
import plugins.slack.api.Pusher;
import plugins.slack.api.ServiceFactory;
import plugins.slack.bean.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlackConfigController extends WindowController {

    /**
     * アクセストークン
     */
    @FXML
    private TextField incomingWebhookUrl;

    /**
     * Channel ID
     */
    @FXML
    private TextField channelId;

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
        SlackConfig config = SlackConfig.get();
        incomingWebhookUrl.setText(config.getIncomingWebhookUrl());
        channelId.setText(config.getChannelId());
        notifyMissionCompleted.setSelected(config.isNotifyMissionCompleted());
        notifyNdockCompleted.setSelected(config.isNotifyNdockCompleted());
    }

    /**
     * @param event ActionEvent
     */
    @FXML
    void load(@SuppressWarnings("UnusedParameters") ActionEvent event) {
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
        SlackConfig config = SlackConfig.get();
        config.setIncomingWebhookUrl(incomingWebhookUrl.getText());
        config.setChannelId(channelId.getText());
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
        new Pusher(incomingWebhookUrl.getText()).pushToSelectedTargets(
                "送信テスト",
                "航海日誌 Slack Plugin より",
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
        Alert alert = new Alert(alertType, contentText, ButtonType.OK);
        alert.initOwner(getWindow().getOwner());
        alert.show();
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(SlackConfigController.class);

        private static void logError(Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
