package plugins.slack.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

import java.io.Serializable;

/**
 * Slackの設定
 */
@Data
@NoArgsConstructor
public class SlackConfig implements Serializable {

    private static final long serialVersionUID = 4545996096461149394L;

    /**
     * Incoming Webhook URL
     */
    private String incomingWebhookUrl;

    /**
     * 遠征完了を通知する
     */
    private boolean notifyMissionCompleted = true;

    /**
     * 入渠完了を通知する
     */
    private boolean notifyNdockCompleted = true;

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link SlackConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(SlackConfig.class, SlackConfig::new)</code>
     * </blockquote>
     *
     * @return {@link SlackConfig}
     */
    public static SlackConfig get() {
        return ConfigLoader.load(SlackConfig.class, SlackConfig::new);
    }
}
