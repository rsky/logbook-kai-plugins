package pushbullet.bean;

import logbook.internal.Config;

import java.io.Serializable;

/**
 * Pushbulletの設定
 */
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

    /**
     * アクセストークンを取得します
     *
     * @return String
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * アクセストークンを設定します
     *
     * @param accessToken String
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 遠征完了通知を取得します
     *
     * @return boolean
     */
    public boolean isNotifyMissionCompleted() {
        return notifyMissionCompleted;
    }

    /**
     * 遠征完了通知を設定します
     *
     * @param notifyMissionCompleted boolean
     */
    public void setNotifyMissionCompleted(boolean notifyMissionCompleted) {
        this.notifyMissionCompleted = notifyMissionCompleted;
    }

    /**
     * 入渠完了通知を取得します
     *
     * @return boolean
     */
    public boolean isNotifyNdockCompleted() {
        return notifyNdockCompleted;
    }

    /**
     * 入渠完了通知を設定します
     *
     * @param notifyNDockCompleted boolean
     */
    public void setNotifyNdockCompleted(boolean notifyNDockCompleted) {
        this.notifyNdockCompleted = notifyNDockCompleted;
    }
}
