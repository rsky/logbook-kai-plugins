package plugins.discord.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class Pusher {
    private final String incomingWebhookUrl;

    public Pusher(String incomingWebhookUrl) {
        this.incomingWebhookUrl = incomingWebhookUrl;
    }

    /**
     * @param title   String
     * @param message String
     */
    public void push(String title, String message) {
        push(title, message, null, null);
    }

    /**
     * @param title     String
     * @param message   String
     * @param onSuccess Consumer&lt;String&gt;
     * @param onError   Consumer&lt;Throwable&gt;
     */
    public void push(String title,
                     String message,
                     Consumer<String> onSuccess,
                     Consumer<Throwable> onError) {
        var body = Json.createObjectBuilder()
                .add("embeds", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("title", title)
                                .add("description", message)
                        )
                )
                .build()
                .toString();

        try (var client = HttpClient.newHttpClient()) {
            var req = HttpRequest.newBuilder(URI.create(this.incomingWebhookUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .setHeader("Content-Type", "application/json")
                    .build();
            try {
                var res = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() == 200 && onSuccess != null) {
                    onSuccess.accept(res.body());
                }
            } catch (Exception e) {
                if (onError != null) {
                    onError.accept(e);
                } else {
                    LoggerHolder.logError(e);
                }
            }
        }
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(Pusher.class);

        private static void logError(Throwable e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
