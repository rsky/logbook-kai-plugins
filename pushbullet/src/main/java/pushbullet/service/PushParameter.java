package pushbullet.service;

import com.google.gson.annotations.SerializedName;
import lombok.Value;
import pushbullet.bean.Channel;
import pushbullet.bean.Device;

@Value
public class PushParameter {
    private String type;

    private String title;

    private String body;

    @SerializedName("device_iden")
    private String deviceIdentity;

    @SerializedName("channel_tag")
    private String channelTag;

    public static PushParameter noteToDevice(Device device, String title, String body) {
        return new PushParameter(PushType.NOTE, title, body, device.getIdentity(), null);
    }

    public static PushParameter noteToChannel(Channel channel, String title, String body) {
        return new PushParameter(PushType.NOTE, title, body, null, channel.getTag());
    }

    private static class PushType {
        private static final String NOTE = "note";
    }
}
