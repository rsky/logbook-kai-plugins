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

    @FXML
    private TextField rankNo;

    @FXML
    private TextField rate1;

    @FXML
    private TextField rate2;

    @FXML
    private TextField factor;

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
        String actualRateStr = rate2.getText();
        if (actualRateStr.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "実戦果を入力してください", ButtonType.OK);
            alert.show();
            return;
        }

        int actualRate;
        try {
            actualRate = Integer.valueOf(actualRateStr);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "実戦果を正しく入力してください", ButtonType.OK);
            alert.show();
            return;
        }

        int userRateFactor = Calculator.detectUserRateFactor(
                config.getLastRankNo(), config.getLastObfuscatedRate(), actualRate);

        if (userRateFactor > 0) {
            factor.setText(String.valueOf(userRateFactor));

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    String.format("戦果係数は%dです", userRateFactor), ButtonType.OK);
            alert.show();

            // 戦果係数を更新・保存
            RankingChartConfig currentConfig = RankingChartConfig.get();
            currentConfig.setUserRateFactor(userRateFactor);
            ThreadManager.getExecutorService().execute(Config.getDefault()::store);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "戦果係数の計算に失敗しました", ButtonType.OK);
            alert.show();
        }
    }
}
