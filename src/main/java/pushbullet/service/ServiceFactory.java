package pushbullet.service;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    private static final String BASE_URL = "https://service.pushbullet.com/v2/";

    public static PushbulletService create(String accessToken) {
        return buildRetrofit(accessToken).create(PushbulletService.class);
    }

    private static Retrofit buildRetrofit(String accessToken) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .addHeader("Access-Token", accessToken)
                        .build()))
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}
