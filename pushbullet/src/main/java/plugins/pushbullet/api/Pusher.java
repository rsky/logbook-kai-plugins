package plugins.pushbullet.api;

import io.reactivex.schedulers.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.pushbullet.bean.Channel;
import plugins.pushbullet.bean.ChannelCollection;
import plugins.pushbullet.bean.Device;
import plugins.pushbullet.bean.DeviceCollection;

public class Pusher {
    private String accessToken;

    public Pusher(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 設定で選択した端末およびチャンネルにプッシュする
     *
     * @param title   String
     * @param message String
     */
    public void pushToSelectedTargets(String title, String message) {
        PushbulletService service = ServiceFactory.create(accessToken);

        DeviceCollection.get()
                .stream()
                .filter(Device::isActive)
                .filter(Device::isSelected)
                .forEach(device -> service.push(PushParameter.noteToDevice(device, title, message))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(result -> LoggerHolder.LOG.debug(result.getPushes()), LoggerHolder::logError));

        ChannelCollection.get()
                .stream()
                .filter(Channel::isActive)
                .filter(Channel::isSelected)
                .forEach(channel -> service.push(PushParameter.noteToChannel(channel, title, message))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(result -> LoggerHolder.LOG.debug(result.getPushes()), LoggerHolder::logError));
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(Pusher.class);

        private static void logError(Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
