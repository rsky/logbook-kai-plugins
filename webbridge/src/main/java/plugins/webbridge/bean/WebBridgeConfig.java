package plugins.webbridge.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class WebBridgeConfig implements Serializable {
    private static final long serialVersionUID = 1325064620901096791L;

    /**
     * データ送信先ホスト名
     */
    private String targetHost = "127.0.0.1";

    /**
     * データ送信先ポート番号
     */
    private int targetPort = 10080;

    /**
     * データ送信が有効かどうか
     */
    private boolean publishEnabled = true;

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link WebBridgeConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(WebBridgeConfig.class, WebBridgeConfig::new)</code>
     * </blockquote>
     *
     * @return {@link WebBridgeConfig}
     */
    public static WebBridgeConfig get() {
        return ConfigLoader.load(WebBridgeConfig.class, WebBridgeConfig::new);
    }
}
