package plugins.webbridge.gui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.internal.gui.WindowController;
import plugins.webbridge.bean.WebBridgeConfig;

public class WebBridgeConfigController extends WindowController {

    /**
     * 中継サーバーへのデータ送信を有効にする
     */
    @FXML
    private CheckBox bridgeEnabled;

    /**
     *  ホスト
     */
    @FXML
    private TextField bridgeHost;

    /**
     * ポート番号
     */
    @FXML
    private TextField bridgePort;

    /**
     * 初期化
     */
    @SuppressWarnings("unused")
    @FXML
    void initialize() {
        WebBridgeConfig config = WebBridgeConfig.get();
        bridgeEnabled.setSelected(config.isBridgeEnabled());
        bridgeHost.setText(config.getBridgeHost());
        bridgePort.setText(String.valueOf(config.getBridgePort()));
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
        WebBridgeConfig config = WebBridgeConfig.get();
        config.setBridgeEnabled(bridgeEnabled.isSelected());
        config.setBridgeHost(bridgeHost.getText());
        config.setBridgePort(Integer.parseInt(bridgePort.getText()));

        ThreadManager.getExecutorService()
                .execute(Config.getDefault()::store);
        getWindow().close();
    }
}
