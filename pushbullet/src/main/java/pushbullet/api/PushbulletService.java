package pushbullet.api;

import pushbullet.api.entity.Channels;
import pushbullet.api.entity.Devices;
import pushbullet.api.entity.Pushes;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Pushbulletクライアント
 */
public interface PushbulletService {
    @GET("channels")
    Observable<Channels> getChannels();

    @GET("devices")
    Observable<Devices> getDevices();

    @POST("pushes")
    Observable<Pushes> push(@Body PushParameter param);
}
