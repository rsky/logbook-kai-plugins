package pushbullet.bean;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 端末情報と送信の可否
 */
@Data
public class Device implements Serializable {

    private static final long serialVersionUID = 8039039160708225609L;

    @Getter(AccessLevel.NONE)
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    @SerializedName("iden")
    private String identity;
    private String nickname;
    private boolean active;

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
