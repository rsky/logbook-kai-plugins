package pushbullet.bean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * チャンネル情報と送信の可否
 */
@Data
public class Channel implements Serializable {

    private static final long serialVersionUID = 408581662007793999L;

    @Getter(AccessLevel.NONE)
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private String tag;
    private String name;
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
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return String.format("<%s>", tag);
    }
}
