package plugins.pushbullet.api.entity;

import lombok.Value;
import plugins.pushbullet.bean.Channel;

import java.util.List;

@Value
public class Channels {
    List<Channel> channels;
}
