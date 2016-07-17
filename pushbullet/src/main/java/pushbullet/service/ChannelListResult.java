package pushbullet.service;

import lombok.Value;
import pushbullet.bean.Channel;

import java.util.List;

@Value
public  class ChannelListResult {
    private List<Channel> channels;
}
