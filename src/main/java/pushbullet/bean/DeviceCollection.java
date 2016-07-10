package pushbullet.bean;

import logbook.internal.Config;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 端末のコレクション
 */
public class DeviceCollection implements Serializable {
    private static final long serialVersionUID = -8854590875317234076L;

    /**
     * 端末
     */
    private Map<String, Device> deviceMap = new LinkedHashMap<>();

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link DeviceCollection}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(DeviceCollection.class, DeviceCollection::new)</code>
     * </blockquote>
     *
     * @return {@link DeviceCollection}
     */
    public static DeviceCollection get() {
        return Config.getDefault().get(DeviceCollection.class, DeviceCollection::new);
    }

    /**
     * 端末を取得します
     *
     * @return 端末
     */
    public Map<String, Device> getDeviceMap() {
        return this.deviceMap;
    }

    /**
     * 端末を設定します
     *
     * @param deviceMap 端末
     */
    public void setDeviceMap(Map<String, Device> deviceMap) {
        this.deviceMap = deviceMap;
    }

    public Stream<Device> stream() {
        return getDeviceMap().values().stream();
    }
}
