package plugins.slack.api;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.slack.api.entity.Pushes;
import plugins.slack.bean.Channel;
import plugins.slack.bean.ChannelCollection;
import plugins.slack.bean.Device;
import plugins.slack.bean.DeviceCollection;

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
        pushToSelectedTargets(title, message, null, null);
    }

    /**
     * 設定で選択した端末およびチャンネルにプッシュする
     *
     * @param title   String
     * @param message String
     * @param onSuccess Consumer&lt;Pushes&gt;
     * @param onError Consumer&lt;Throwable&gt;
     */
    public void pushToSelectedTargets(String title,
                                      String message,
                                      Consumer<Pushes> onSuccess,
                                      Consumer<Throwable> onError) {
        SlackService service = ServiceFactory.create(accessToken);
        Consumer<Pushes> _onSuccess = (onSuccess != null) ? onSuccess : pushes -> {};
        Consumer<Throwable> _onError = (onError != null) ? onError : LoggerHolder::logError;

        DeviceCollection.get()
                .stream()
                .filter(Device::isActive)
                .filter(Device::isSelected)
                .forEach(device -> service.push(PushParameter.noteToDevice(device, title, message))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(_onSuccess, _onError));

        ChannelCollection.get()
                .stream()
                .filter(Channel::isActive)
                .filter(Channel::isSelected)
                .forEach(channel -> service.push(PushParameter.noteToChannel(channel, title, message))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(_onSuccess, _onError));
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
