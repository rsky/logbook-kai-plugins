package plugins.slack.api;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import io.reactivex.functions.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pusher {
    private final String incomingWebhookUrl;

    public Pusher(String incomingWebhookUrl) {
        this.incomingWebhookUrl = incomingWebhookUrl;
    }

    /**
     * @param title String
     * @param message String
     */
    public void push(String title, String message) {
        push(title, message, null, null);
    }

    /**
     * @param title String
     * @param message String
     * @param onSuccess Consumer&lt;WebhookResponse&gt;
     * @param onError Consumer&lt;Throwable&gt;
     */
    public void push(String title,
                     String message,
                     Consumer<WebhookResponse> onSuccess,
                     Consumer<Throwable> onError) {
        Slack slack = Slack.getInstance();
        Payload payload = Payload.builder()
                .text("*" + title + "*\n" + message)
                .build();

        try {
            WebhookResponse response = slack.send(this.incomingWebhookUrl, payload);
            if (onSuccess != null) {
                onSuccess.accept(response);
            }
        } catch (Exception e) {
            if (onError != null) {
                try {
                    onError.accept(e);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                LoggerHolder.logError(e);
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
