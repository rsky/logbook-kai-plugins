package plugins.pushbullet.api;

import com.google.gson.annotations.SerializedName;
import lombok.Value;
import plugins.pushbullet.bean.Channel;
import plugins.pushbullet.bean.Device;

@Value
class PushParameter {
    private String type;

    private String title;

    private String body;

    @SerializedName("device_iden")
    private String deviceIdentity;

    @SerializedName("channel_tag")
    private String channelTag;

    static PushParameter noteToDevice(Device device, String title, String body) {
        return new PushParameter(PushType.NOTE, title, body, device.getIdentity(), null);
    }

    static PushParameter noteToChannel(Channel channel, String title, String body) {
        return new PushParameter(PushType.NOTE, title, body, null, channel.getTag());
    }

    private static class PushType {
        private static final String NOTE = "note";
    }
}
