package pushbullet.service;

import java.util.List;

public class PushResult {
    private List<SimplePushResult> pushes;

    public List<SimplePushResult> getPushes() {
        return pushes;
    }

    class SimplePushResult {
        public String iden;
    }
}
