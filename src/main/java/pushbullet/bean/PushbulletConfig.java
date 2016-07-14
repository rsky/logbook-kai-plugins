package pushbullet.bean;

import logbook.internal.Config;
import lombok.Data;

import java.io.Serializable;

/**
 * Pushbulletの設定
 */
@Data
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
        return Config.getDefault().get(PushbulletConfig.class, PushbulletConfig::new);
    }
}
