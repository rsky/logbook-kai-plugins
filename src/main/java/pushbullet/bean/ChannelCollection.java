package pushbullet.bean;

import logbook.internal.Config;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * チャンネルのコレクション
 */
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

    /**
     * チャンネルを取得します
     *
     * @return チャンネル
     */
    public Map<String, Channel> getChannelMap() {
        return this.channelMap;
    }

    /**
     * チャンネルを設定します
     *
     * @param channelMap チャンネル
     */
    public void setChannelMap(Map<String, Channel> channelMap) {
        this.channelMap = channelMap;
    }

    public Stream<Channel> stream() {
        return getChannelMap().values().stream();
    }
}
