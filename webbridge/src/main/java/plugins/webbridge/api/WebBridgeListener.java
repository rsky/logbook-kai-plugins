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

public class WebBridgeListener implements APIListenerSpi {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void accept(JsonObject jsonObject, RequestMetaData requestMetaData, ResponseMetaData responseMetaData) {
        try {
            JsonObject bodyJson = Json.createObjectBuilder()
                    .add("requestURI", requestMetaData.getRequestURI())
                    .add("responseJSON", jsonObject)
                    .build();
            RequestBody body = RequestBody.create(bodyJson.toString(), JSON);

            int port = WebBridgeConfig.get().getPort();
            String url = "http://127.0.0.1:" + port + "/api";

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
