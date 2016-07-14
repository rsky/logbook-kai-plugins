package pushbullet.bean;

import logbook.internal.Config;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * チャンネルのコレクション
 */
@Data
public class ChannelCollection implements Serializable {

    private static final long serialVersionUID = 4563418961786537941L;

    /**
     * チャンネル
     */
    private Map<String, Channel> channelMap = new LinkedHashMap<>();

    /**
     * アプリケーションのデフォルト設定ディレクトリから{@link ChannelCollection}を取得します、
     * これは次の記述と同等です
     * <blockquote>
     * <code>Config.getDefault().get(ChannelCollection.class, ChannelCollection:new)</code>
     * </blockquote>
     *
     * @return {@link ChannelCollection}
     */
    public static ChannelCollection get() {
        return Config.getDefault().get(ChannelCollection.class, ChannelCollection::new);
    }

    public Stream<Channel> stream() {
        return getChannelMap().values().stream();
    }
}
