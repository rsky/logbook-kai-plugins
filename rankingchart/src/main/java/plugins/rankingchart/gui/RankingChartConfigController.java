package plugins.rankingchart.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.internal.gui.WindowController;
import plugins.rankingchart.bean.RankingChartConfig;
import plugins.rankingchart.util.Calculator;

public class RankingChartConfigController extends WindowController {
    private RankingChartConfig config;

    /** 順位 */
    @FXML
    private TextField rankNo;

    /** 仮戦果 */
    @FXML
    private TextField rate1;

    /** 実戦果 */
    @FXML
    private TextField rate2;

    /** 戦果係数 */
    @FXML
    private TextField factor;

    /** 戦果係数の更新ボタン */
    @FXML
    private Button button;

    @FXML
    void initialize() {
        config = RankingChartConfig.get();

        if (config.getLastRankNo() > 0) {
            rankNo.setText(String.valueOf(config.getLastRankNo()));
        }
        if (config.getLastObfuscatedRate() > 0) {
            rate1.setText(String.valueOf(config.getLastObfuscatedRate()));
        }
        if (config.getUserRateFactor() > 0) {
            factor.setText(String.valueOf(config.getUserRateFactor()));
        }

        if (rankNo.getText().isEmpty() || rate1.getText().isEmpty()) {
            button.setText("マイランク未取得");
            button.setOnAction(null);
        }
    }

    @FXML
    void calc(@SuppressWarnings("unused") ActionEvent event) {
        var actualRateStr = rate2.getText();
        if (actualRateStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "実戦果を入力してください");
            return;
        }

        int actualRate;
        try {
            actualRate = Integer.valueOf(actualRateStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "実戦果を正しく入力してください");
            return;
        }

        var userRateFactor = Calculator.detectUserRateFactor(
                config.getLastRankNo(), config.getLastObfuscatedRate(), actualRate);

        if (userRateFactor > 0) {
            factor.setText(String.valueOf(userRateFactor));

            showAlert(Alert.AlertType.INFORMATION, String.format("戦果係数は%dです", userRateFactor));

            // 戦果係数を更新・保存
            var currentConfig = RankingChartConfig.get();
            currentConfig.setUserRateFactor(userRateFactor);
            ThreadManager.getExecutorService().execute(Config.getDefault()::store);
        } else {
            showAlert(Alert.AlertType.ERROR, "戦果係数の計算に失敗しました");
        }
    }

    private void showAlert(Alert.AlertType alertType, String contentText) {
        var alert = new Alert(alertType, contentText, ButtonType.OK);
        alert.initOwner(getWindow().getOwner());
        alert.show();
    }
}
