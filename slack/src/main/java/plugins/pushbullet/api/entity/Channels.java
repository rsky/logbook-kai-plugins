package plugins.slack.api.entity;

import lombok.Value;
import plugins.slack.bean.Channel;

import java.util.List;

@Value
public class Channels {
    List<Channel> channels;
}
