package pushbullet.bean;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Serializable;

/**
 * 端末情報と送信の可否
 */
public class Device implements Serializable {
    private static final long serialVersionUID = 8039039160708225609L;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    @SerializedName("iden")
    private String identity;
    private String nickname;
    private boolean active;

    /**
     * identityを取得します
     *
     * @return String
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * identityを設定します
     *
     * @param identity String
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * 端末のニックネームを取得します
     *
     * @return String
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 端末のニックネームを設定します
     *
     * @param nickname String
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 端末が有効かどうかを取得します
     *
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 端末が有効かどうかを設定します
     *
     * @param active boolean
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 通知の可否プロパティオブジェクトを取得します
     *
     * @return BooleanProperty
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * 通知の可否を取得します
     *
     * @return boolean
     */
    public boolean isSelected() {
        return selectedProperty().get();
    }

    /**
     * 通知の可否を設定します
     *
     * @param selected boolean
     */
    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    @Override
    public String toString() {
        if (nickname != null && !nickname.isEmpty()) {
            return nickname;
        }
        return String.format("<%s>", identity);
    }
}
