package pushbullet.service;

import com.google.gson.annotations.SerializedName;
import pushbullet.bean.Channel;
import pushbullet.bean.Device;

public class PushParameter {
    private String type;

    private String title;

    private String body;

    @SerializedName("device_iden")
    private String deviceIdentity;

    @SerializedName("channel_tag")
    private String channelTag;

    private PushParameter() {
    }

    public static PushParameter noteToDevice(Device device, String title, String body) {
        PushParameter param = new PushParameter();
        param.type = PushType.NOTE;
        param.title = title;
        param.body = body;
        param.deviceIdentity = device.getIdentity();
        return param;
    }

    public static PushParameter noteToChannel(Channel channel, String title, String body) {
        PushParameter param = new PushParameter();
        param.type = PushType.NOTE;
        param.title = title;
        param.body = body;
        param.channelTag = channel.getTag();
        return param;
    }

    private static class PushType {
        private static final String NOTE = "note";
    }
}
