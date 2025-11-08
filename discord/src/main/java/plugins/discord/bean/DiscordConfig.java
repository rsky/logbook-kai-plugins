package plugins.discord.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

import java.io.Serializable;

/**
 * Discordの設定
 */
@Data
@NoArgsConstructor
public class DiscordConfig implements Serializable {

    private static final long serialVersionUID = -5955068181431698263L;

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
     * アプリケーションのデフォルト設定ディレクトリから{@link DiscordConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(DiscordConfig.class, DiscordConfig::new)</code>
     * </blockquote>
     *
     * @return {@link DiscordConfig}
     */
    public static DiscordConfig get() {
        return ConfigLoader.load(DiscordConfig.class, DiscordConfig::new);
    }
}
