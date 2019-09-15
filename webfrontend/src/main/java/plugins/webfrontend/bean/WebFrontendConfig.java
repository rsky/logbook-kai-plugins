package plugins.webfrontend.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

import java.io.Serializable;

/**
 * Web Frontendの設定
 */
@Data
@NoArgsConstructor
public class WebFrontendConfig implements Serializable {
    private static final long serialVersionUID = -6824546006976973959L;

    /**
     * HTTP/WebSocketサーバー用ポート番号
     */
    private int httpPort = 10080;

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link WebFrontendConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(WebFrontendConfig.class, WebFrontendConfig::new)</code>
     * </blockquote>
     *
     * @return {@link WebFrontendConfig}
     */
    public static WebFrontendConfig get() {
        return ConfigLoader.load(WebFrontendConfig.class, WebFrontendConfig::new);
    }
}
