package plugins.slack.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import plugins.util.ConfigLoader;

import java.io.Serializable;

/**
 * Pushbulletの設定
 */
@Data
@NoArgsConstructor
public class PushbulletConfig implements Serializable {

    private static final long serialVersionUID = -317595360788883807L;

    /**
     * APIトークン
     */
    private String accessToken;

    /**
     * 遠征完了を通知する
     */
    private boolean notifyMissionCompleted = true;

    /**
     * 入渠完了を通知する
     */
    private boolean notifyNdockCompleted = true;

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link PushbulletConfig}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(PushbulletConfig.class, PushbulletConfig::new)</code>
     * </blockquote>
     *
     * @return {@link PushbulletConfig}
     */
    public static PushbulletConfig get() {
        return ConfigLoader.load(PushbulletConfig.class, PushbulletConfig::new);
    }
}
