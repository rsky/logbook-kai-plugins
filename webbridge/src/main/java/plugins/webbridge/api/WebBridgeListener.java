package plugins.webbridge.api;

import logbook.api.APIListenerSpi;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.recipes.GzipRequestInterceptor;
import plugins.webbridge.bean.WebBridgeConfig;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Date;

public class WebBridgeListener implements APIListenerSpi {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    private OkHttpClient gzipClient = new OkHttpClient.Builder()
            .addInterceptor(new GzipRequestInterceptor())
            .build();

    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        WebBridgeConfig config = WebBridgeConfig.get();
        if (!config.isBridgeEnabled()) {
            return;
        }

        String apiURI = requestMetaData.getRequestURI();
        String url = "http://" + config.getBridgeHost() + ":" + config.getBridgePort() + "/pub";

        RequestBody body = RequestBody.create(Json.createObjectBuilder()
                .add("uri", apiURI)
                .add("time", new Date().getTime())
                .add("body", jsonObject)
                .build().toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .header("X-API-URI", apiURI)
                .post(body)
                .build();

        try {
            if (apiURI.equals("/kcsapi/api_start2/getData")) {
                this.gzipClient.newCall(request).execute();
            } else {
                this.client.newCall(request).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
