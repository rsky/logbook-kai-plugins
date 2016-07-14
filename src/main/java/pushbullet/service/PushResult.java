package pushbullet.service;

import lombok.Value;

import java.util.List;

@Value
public class PushResult {
    private List<SimplePushResult> pushes;

    @Value
    public class SimplePushResult {
        private String iden;
    }
}
