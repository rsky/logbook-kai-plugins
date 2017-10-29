package plugins.pushbullet.gui;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import logbook.internal.Config;
import logbook.internal.ThreadManager;
import logbook.internal.gui.WindowController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plugins.pushbullet.api.PushbulletService;
import plugins.pushbullet.api.Pusher;
import plugins.pushbullet.api.ServiceFactory;
import plugins.pushbullet.bean.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushbulletConfigController extends WindowController {

    /**
     * アクセストークン
     */
    @FXML
    private TextField accessToken;

    /**
     * 遠征完了を通知する
     */
    @FXML
    private CheckBox notifyMissionCompleted;

    /**
     * 入渠完了を通知する
     */
    @FXML
    private CheckBox notifyNdockCompleted;

    /**
     * 端末リスト
     */
    @FXML
    private ListView<Device> deviceListView;
    private ObservableList<Device> devices = FXCollections.observableArrayList();
    private Map<String, Boolean> deviceSelectedMap = new ConcurrentHashMap<>();

    /**
     * 自分がオーナーのチャンネルリスト
     */
    @FXML
    private ListView<Channel> channelListView;
    private ObservableList<Channel> channels = FXCollections.observableArrayList();
    private Map<String, Boolean> channelSelectedMap = new ConcurrentHashMap<>();

    private void addDevice(Device device) {
        devices.add(device);
        deviceSelectedMap.put(device.getIdentity(), device.isSelected());
    }

    private Device markDeviceSelected(Device device) {
        device.setSelected(deviceSelectedMap.getOrDefault(device.getIdentity(), false));
        return device;
    }

    private void addChannel(Channel channel) {
        channels.add(channel);
        channelSelectedMap.put(channel.getTag(), channel.isSelected());
    }

    private Channel markChannelSelected(Channel channel) {
        channel.setSelected(channelSelectedMap.getOrDefault(channel.getTag(), false));
        return channel;
    }

    private <E> void setUpListChangeListener(ObservableList<E> list, ListView<E> listView) {
        final double CELL_HEIGHT = 25.0;
        list.addListener((ListChangeListener<E>) c -> {
            final int size = list.size();
            switch (size) {
                case 0:
                    listView.setMinHeight(0.0);
                    listView.setPrefHeight(0.0);
                    break;
                case 1:
                    listView.setMinHeight(CELL_HEIGHT + 2.0);
                    listView.setPrefHeight(CELL_HEIGHT + 2.0);
                    break;
                default:
                    listView.setMinHeight(CELL_HEIGHT + 2.0);
                    listView.setPrefHeight(CELL_HEIGHT * size + 4.0);
                    break;
            }
        });
    }

    /**
     * 初期化
     */
    @SuppressWarnings("unused")
    @FXML
    void initialize() {
        PushbulletConfig config = PushbulletConfig.get();
        accessToken.setText(config.getAccessToken());
        notifyMissionCompleted.setSelected(config.isNotifyMissionCompleted());
        notifyNdockCompleted.setSelected(config.isNotifyNdockCompleted());

        setUpListChangeListener(devices, deviceListView);
        DeviceCollection.get()
                .stream()
                .forEach(this::addDevice);
        deviceListView.setItems(devices);
        deviceListView.setCellFactory(listView -> {
            CheckBoxListCell<Device> cell = new CheckBoxListCell<>();
            cell.setSelectedStateCallback(Device::selectedProperty);
            return cell;
        });

        setUpListChangeListener(channels, channelListView);
        ChannelCollection.get()
                .stream()
                .forEach(this::addChannel);
        channelListView.setItems(channels);
        channelListView.setCellFactory(listView -> {
            CheckBoxListCell<Channel> cell = new CheckBoxListCell<>();
            cell.setSelectedStateCallback(Channel::selectedProperty);
            return cell;
        });
    }

    /**
     * PushBulletからAPIトークンに紐付けされているデバイスとチャンネルを取得する
     *
     * @param event ActionEvent
     */
    @FXML
    void load(@SuppressWarnings("UnusedParameters") ActionEvent event) {
        String accessToken = this.accessToken.getText();
        if (accessToken.isEmpty()) {
            return;
        }
        PushbulletService service = ServiceFactory.create(accessToken);

        devices.clear();
        service.getDevices()
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .flatMapObservable(response -> Observable.fromIterable(response.getDevices()))
                .filter(Device::isActive)
                .map(this::markDeviceSelected)
                .subscribe(this::addDevice, LoggerHolder::logError);

        channels.clear();
        service.getChannels()
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .flatMapObservable(response -> Observable.fromIterable(response.getChannels()))
                .filter(Channel::isActive)
                .map(this::markChannelSelected)
                .subscribe(this::addChannel, LoggerHolder::logError);
    }

    /**
     * キャンセル
     */
    @FXML
    void cancel() {
        getWindow().close();
    }

    /**
     * 設定の反映
     */
    @FXML
    void ok() {
        PushbulletConfig config = PushbulletConfig.get();
        config.setAccessToken(accessToken.getText());
        config.setNotifyMissionCompleted(notifyMissionCompleted.isSelected());
        config.setNotifyNdockCompleted(notifyNdockCompleted.isSelected());

        DeviceCollection deviceCollection = DeviceCollection.get();
        Map<String, Device> deviceMap = new LinkedHashMap<>();
        devices.forEach(d -> deviceMap.put(d.getIdentity(), d));
        deviceCollection.setDeviceMap(deviceMap);

        ChannelCollection channelCollection = ChannelCollection.get();
        Map<String, Channel> channelMap = new LinkedHashMap<>();
        channels.forEach(c -> channelMap.put(c.getTag(), c));
        channelCollection.setChannelMap(channelMap);

        ThreadManager.getExecutorService()
                .execute(Config.getDefault()::store);
        getWindow().close();
    }

    /**
     * テスト通知
     */
    @FXML
    void test() {
        new Pusher(accessToken.getText()).pushToSelectedTargets(
                "送信テスト",
                "航海日誌 Pushbullet Plugin より",
                pushes -> Platform.runLater(() ->
                        showAlert(Alert.AlertType.INFORMATION, "テスト通知を送信しました")
                ),
                throwable -> {
                    LoggerHolder.logError(throwable);
                    Platform.runLater(() ->
                            showAlert(Alert.AlertType.ERROR, "テスト通知の送信に失敗しました")
                    );
                });
    }

    private void showAlert(Alert.AlertType alertType, String contentText) {
        Alert alert = new Alert(alertType, contentText, ButtonType.OK);
        alert.initOwner(getWindow().getOwner());
        alert.show();
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(PushbulletConfigController.class);

        private static void logError(Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
