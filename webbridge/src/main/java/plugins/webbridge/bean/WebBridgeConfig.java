package plugins.webbridge.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class WebBridgeConfig implements Serializable {
    private static final long serialVersionUID = 4802270354093602166L;

    /**
     * ポート番号
     */
    private int port = 10080;

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
