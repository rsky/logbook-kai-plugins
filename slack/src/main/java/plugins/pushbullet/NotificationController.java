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
import plugins.slack.api.ServiceFactory;
import plugins.slack.bean.SlackConfig;
import plugins.slack.gui.SlackConfigMenu;

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

    private Map<Integer, Long> timeStampMission = new HashMap<>();

    /** 入渠通知のタイムスタンプ */
    private Map<Integer, Long> timeStampNdock = new HashMap<>();

    @Override
    public void run() {
        String accessToken = SlackConfig.get().getAccessToken();
        if (accessToken != null) {
            // warm-up
            ServiceFactory.create(accessToken).getUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(user -> {}, LoggerHolder::logError);
        }

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
            SlackConfig config = SlackConfig.get();
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
        Map<Integer, DeckPort> ports = DeckPortCollection.get()
                .getDeckPortMap();
        long currentTime = System.currentTimeMillis();

        for (DeckPort port : ports.values()) {
            // 0=未出撃, 1=遠征中, 2=遠征帰還, 3=遠征中止
            int state = port.getMission().get(0).intValue();
            // 帰還時間
            long time = port.getMission().get(2);

            if (0 == state) {
                timeStampMission.put(port.getId(), 0L);
            } else {
                // 残り時間を計算
                Duration now = Duration.ofMillis(time - currentTime);
                // 前回の通知の時間
                long timeStamp = timeStampMission.getOrDefault(port.getId(), 0L);
                if (requireNotify(now, timeStamp)) {
                    timeStampMission.put(port.getId(), System.currentTimeMillis());
                    SlackNotifyMission(port);
                }
            }
        }
    }


    /**
     * Slackで遠征通知
     *
     * @param port 艦隊
     */
    private void SlackNotifyMission(DeckPort port) {
        String title = String.format("遠征完了 #%d", port.getId());
        String message = Messages.getString("mission.complete", port.getName());
        SlackNotify(title, message);
    }

    /**
     * 入渠ドックの通知をチェックします
     */
    private void checkNotifyNdock() {
        Map<Integer, Ndock> ndockMap = NdockCollection.get()
                .getNdockMap();
        long currentTime = System.currentTimeMillis();

        for (Ndock ndock : ndockMap.values()) {
            // 完了時間
            long time = ndock.getCompleteTime();

            if (1 > time) {
                timeStampNdock.put(ndock.getId(), 0L);
            } else {
                // 残り時間を計算
                Duration now = Duration.ofMillis(time - currentTime);
                // 前回の通知の時間
                long timeStamp = timeStampNdock.getOrDefault(ndock.getId(), 0L);

                if (requireNotify(now, timeStamp)) {
                    timeStampNdock.put(ndock.getId(), currentTime);
                    SlackNotifyNdock(ndock);
                }
            }
        }
    }

    /**
     * Slackで入渠ドックの通知
     *
     * @param ndock 入渠ドック
     */
    private void SlackNotifyNdock(Ndock ndock) {
        String title = String.format("修復完了 #%d", ndock.getId());
        Ship ship = ShipCollection.get()
                .getShipMap()
                .get(ndock.getShipId());
        String name = Ships.shipMst(ship)
                .map(ShipMst::getName)
                .orElse("");
        String message = Messages.getString("ship.ndock", name, ship.getLv()); //$NON-NLS-1$

        SlackNotify(title, message);
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
            Duration course = Duration.ofMillis(System.currentTimeMillis() - timeStamp);
            // リマインド間隔
            Duration interval = Duration.ofSeconds(AppConfig.get().getRemind());
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
    private void SlackNotify(String title, String message) {
        String accessToken = SlackConfig.get().getAccessToken();
        if (accessToken != null) {
            new Pusher(accessToken).pushToSelectedTargets(title, message);
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
