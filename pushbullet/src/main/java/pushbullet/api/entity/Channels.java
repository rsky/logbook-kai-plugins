package pushbullet.api.entity;

import lombok.Value;
import pushbullet.bean.Channel;

import java.util.List;

@Value
public class Channels {
    private List<Channel> channels;
}
