package pushbullet.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import logbook.Messages;
import logbook.bean.*;
import logbook.internal.Ships;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pushbullet.api.Pusher;
import pushbullet.bean.PushbulletConfig;
import pushbullet.util.ConfigLoader;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * プッシュ通知コントローラ
 * 大半のコードはlogbook-kai本体のMainControllerからコピーしています
 */
class NotificationController {

    /** 通知 */
    private static final Duration NOTIFY = Duration.ofMinutes(1);

    private Map<Integer, Long> timeStampMission = new HashMap<>();

    /** 入渠通知のタイムスタンプ */
    private Map<Integer, Long> timeStampNdock = new HashMap<>();

    /**
     * 通知開始
     */
    void start() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(
                javafx.util.Duration.seconds(1),
                this::update));
        timeline.play();
    }

    /**
     * 通知の更新
     *
     * @param e ActionEvent
     */
    private void update(ActionEvent e) {
        try {
            PushbulletConfig config = PushbulletConfig.get();
            if (config.isNotifyMissionCompleted()) {
                // 遠征の通知
                checkNotifyMission();
            }
            if (config.isNotifyNdockCompleted()) {
                // 入渠ドックの通知
                checkNotifyNdock();
            }
        } catch (Exception ex) {
            LoggerHolder.LOG.error("設定の初期化に失敗しました", ex);
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
                    pushbulletNotifyMission(port);
                }
            }
        }
    }


    /**
     * Pushbulletで遠征通知
     *
     * @param port 艦隊
     */
    private void pushbulletNotifyMission(DeckPort port) {
        String title = String.format("遠征完了 #%d", port.getId());
        String message = Messages.getString("mission.complete", port.getName());
        pushbulletNotify(title, message);
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
                    pushbulletNotifyNdock(ndock);
                }
            }
        }
    }

    /**
     * Pushbulletで入渠ドックの通知
     *
     * @param ndock 入渠ドック
     */
    private void pushbulletNotifyNdock(Ndock ndock) {
        String title = String.format("修復完了 #%d", ndock.getId());
        Ship ship = ShipCollection.get()
                .getShipMap()
                .get(ndock.getShipId());
        String name = Ships.shipMst(ship)
                .map(ShipMst::getName)
                .orElse("");
        String message = Messages.getString("ship.ndock", name, ship.getLv()); //$NON-NLS-1$

        pushbulletNotify(title, message);
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
     * Pushbullet通知
     *
     * @param title String
     * @param message String
     */
    private void pushbulletNotify(String title, String message) {
        String accessToken = PushbulletConfig.get().getAccessToken();
        if (accessToken != null) {
            new Pusher(accessToken).pushToSelectedTargets(title, message);
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(PushbulletConfigMenu.class);
    }
}
