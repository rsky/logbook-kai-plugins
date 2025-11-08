package plugins.slack;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import logbook.Messages;
import logbook.bean.*;
import logbook.internal.Ships;
import logbook.plugin.lifecycle.StartUp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.slack.api.Pusher;
import plugins.slack.bean.SlackConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * プッシュ通知コントローラ
 * 大半のコードはlogbook-kai本体のMainControllerからコピーしています
 */
public class NotificationController implements StartUp {

    /** 通知 */
    private static final Duration NOTIFY = Duration.ofMinutes(1);

    private final Map<Integer, Long> timeStampMission = new HashMap<>();

    /** 入渠通知のタイムスタンプ */
    private final Map<Integer, Long> timeStampNdock = new HashMap<>();

    @Override
    public void run() {
        // 起動時には通知を飛ばさないように1分待ってから監視を始める
        Flowable.interval(60, 1, TimeUnit.SECONDS)
                .observeOn(Schedulers.computation())
                .subscribe(l -> update(), LoggerHolder::logError);
    }

    /**
     * 通知の更新
     */
    private void update() {
        try {
            var config = SlackConfig.get();
            if (config.isNotifyMissionCompleted()) {
                // 遠征の通知
                checkNotifyMission();
            }
            if (config.isNotifyNdockCompleted()) {
                // 入渠ドックの通知
                checkNotifyNdock();
            }
        } catch (Exception e) {
            LoggerHolder.LOG.error("設定の初期化に失敗しました", e);
        }
    }

    /**
     * 遠征の通知をチェックします
     */
    private void checkNotifyMission() {
        var ports = DeckPortCollection.get().getDeckPortMap();
        var currentTime = System.currentTimeMillis();

        for (var port : ports.values()) {
            // 0=未出撃, 1=遠征中, 2=遠征帰還, 3=遠征中止
            var state = port.getMission().get(0).intValue();
            // 帰還時間
            var time = port.getMission().get(2);

            if (0 == state) {
                timeStampMission.put(port.getId(), 0L);
            } else {
                // 残り時間を計算
                Duration now = Duration.ofMillis(time - currentTime);
                // 前回の通知の時間
                var timeStamp = timeStampMission.getOrDefault(port.getId(), 0L);
                if (requireNotify(now, timeStamp)) {
                    timeStampMission.put(port.getId(), System.currentTimeMillis());
                    slackNotifyMission(port);
                }
            }
        }
    }


    /**
     * Slackで遠征通知
     *
     * @param port 艦隊
     */
    private void slackNotifyMission(DeckPort port) {
        var title = String.format("遠征完了 #%d", port.getId());
        var message = Messages.getString("mission.complete", port.getName());
        slackNotify(title, message);
    }

    /**
     * 入渠ドックの通知をチェックします
     */
    private void checkNotifyNdock() {
        var ndockMap = NdockCollection.get().getNdockMap();
        var currentTime = System.currentTimeMillis();

        for (Ndock ndock : ndockMap.values()) {
            // 完了時間
            var time = ndock.getCompleteTime();

            if (1 > time) {
                timeStampNdock.put(ndock.getId(), 0L);
            } else {
                // 残り時間を計算
                Duration now = Duration.ofMillis(time - currentTime);
                // 前回の通知の時間
                var timeStamp = timeStampNdock.getOrDefault(ndock.getId(), 0L);

                if (requireNotify(now, timeStamp)) {
                    timeStampNdock.put(ndock.getId(), currentTime);
                    slackNotifyNdock(ndock);
                }
            }
        }
    }

    /**
     * Slackで入渠ドックの通知
     *
     * @param ndock 入渠ドック
     */
    private void slackNotifyNdock(Ndock ndock) {
        var title = String.format("修復完了 #%d", ndock.getId());
        var ship = ShipCollection.get()
                .getShipMap()
                .get(ndock.getShipId());
        var name = Ships.shipMst(ship)
                .map(ShipMst::getName)
                .orElse("");
        var message = Messages.getString("ship.ndock", name, ship.getLv()); //$NON-NLS-1$

        slackNotify(title, message);
    }

    /**
     * 通知するか判断します
     *
     * @param now 残り時間
     * @param timeStamp 前回の通知の時間
     */
    private boolean requireNotify(Duration now, long timeStamp) {
        if (now.compareTo(NOTIFY) <= 0) {
            // 前回の通知からの経過時間
            var course = Duration.ofMillis(System.currentTimeMillis() - timeStamp);
            // リマインド間隔
            var interval = Duration.ofSeconds(AppConfig.get().getRemind());
            if (course.compareTo(interval) >= 0) {
                if (timeStamp == 0L) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Slack通知
     *
     * @param title String
     * @param message String
     */
    private void slackNotify(String title, String message) {
        var config = SlackConfig.get();
        var incomingWebhookUrl = config.getIncomingWebhookUrl();
        if (incomingWebhookUrl != null && !incomingWebhookUrl.isEmpty()) {
            new Pusher(incomingWebhookUrl).push(title, message);
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(NotificationController.class);

        private static void logError(Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
