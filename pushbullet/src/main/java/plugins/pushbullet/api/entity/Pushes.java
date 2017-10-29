package plugins.pushbullet.api.entity;

import lombok.Value;

import java.util.List;

@Value
public class Pushes {
    List<PushResult> pushes;
}
