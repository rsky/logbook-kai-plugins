package pushbullet.service;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Pushbulletクライアント
 */
public interface PushbulletService {
    @GET("channels")
    Observable<ChannelListResult> getChannels();

    @GET("devices")
    Observable<DeviceListResult> getDevices();

    @POST("pushes")
    Observable<PushResult> push(@Body PushParameter param);
}
