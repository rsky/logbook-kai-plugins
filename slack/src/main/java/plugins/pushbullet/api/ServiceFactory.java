package plugins.slack.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    public static SlackService create(String incomingWebhookUrl) {
        return createRetrofit(incomingWebhookUrl).create(SlackService.class);
    }

    private static Retrofit createRetrofit(String incomingWebhookUrl) {
        return new Retrofit.Builder()
                .baseUrl(incomingWebhookUrl)
                .client(createClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static OkHttpClient createClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .build()))
                .build();
    }
}
