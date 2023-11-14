package plugins.slack.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    private static final String BASE_URL = "https://api.Slack.com/v2/";

    public static SlackService create(String accessToken) {
        return createRetrofit(accessToken).create(SlackService.class);
    }

    private static Retrofit createRetrofit(String accessToken) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(createClient(accessToken))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static OkHttpClient createClient(String accessToken) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .addHeader("Access-Token", accessToken)
                        .build()))
                .build();
    }
}
