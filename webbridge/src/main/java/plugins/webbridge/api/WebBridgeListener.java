package plugins.webbridge.api;

import logbook.api.APIListenerSpi;
import logbook.proxy.RequestMetaData;
import logbook.proxy.ResponseMetaData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import plugins.webbridge.bean.WebBridgeConfig;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Date;

public class WebBridgeListener implements APIListenerSpi {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        WebBridgeConfig config = WebBridgeConfig.get();
        if (!config.isBridgeEnabled()) {
            return;
        }

        String url = "http://" + config.getBridgeHost() + ":" + config.getBridgePort() + "/pub";

        RequestBody body = RequestBody.create(Json.createObjectBuilder()
                .add("uri", requestMetaData.getRequestURI())
                .add("time", new Date().getTime())
                .add("body", jsonObject)
                .build().toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            this.client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
