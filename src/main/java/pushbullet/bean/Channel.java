package pushbullet.bean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Serializable;

/**
 * チャンネル情報と送信の可否
 */
public class Channel implements Serializable {
    private static final long serialVersionUID = 408581662007793999L;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private String tag;
    private String name;
    private boolean active;

    /**
     * tagを取得します
     *
     * @return String
     */
    public String getTag() {
        return tag;
    }

    /**
     * tagを設定します
     *
     * @param tag String
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * チャンネル名を取得します
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * チャンネル名を設定します
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * チャンネルが有効かどうかを取得します
     *
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * チャンネルが有効かどうかを設定します
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
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return String.format("<%s>", tag);
    }
}
