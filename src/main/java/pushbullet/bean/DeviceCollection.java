package pushbullet.bean;

import logbook.internal.Config;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 端末のコレクション
 */
@Data
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

    public Stream<Device> stream() {
        return getDeviceMap().values().stream();
    }
}
