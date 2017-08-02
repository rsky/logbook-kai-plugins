package pushbullet.api;

import io.reactivex.Single;
import pushbullet.api.entity.Channels;
import pushbullet.api.entity.Devices;
import pushbullet.api.entity.Pushes;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Pushbulletクライアント
 */
public interface PushbulletService {
    @GET("channels")
    Single<Channels> getChannels();

    @GET("devices")
    Single<Devices> getDevices();

    @POST("pushes")
    Single<Pushes> push(@Body PushParameter param);
}
