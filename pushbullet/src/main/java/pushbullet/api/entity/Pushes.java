package pushbullet.api.entity;

import lombok.Value;

import java.util.List;

@Value
public class Pushes {
    private List<PushResult> pushes;
}
