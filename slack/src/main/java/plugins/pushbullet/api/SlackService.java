package plugins.slack.api;

import io.reactivex.Single;
import plugins.slack.api.entity.Channels;
import plugins.slack.api.entity.Devices;
import plugins.slack.api.entity.Pushes;
import plugins.slack.api.entity.User;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Slackクライアント
 */
public interface SlackService {
    @GET("users/me")
    Single<User> getUser();

    @GET("channels")
    Single<Channels> getChannels();

    @GET("devices")
    Single<Devices> getDevices();

    @POST("pushes")
    Single<Pushes> push(@Body PushParameter param);
}
