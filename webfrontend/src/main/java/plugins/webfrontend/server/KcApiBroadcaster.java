package plugins.webfrontend.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class KcApiBroadcaster {
    private static final KcApiBroadcaster INSTANCE = new KcApiBroadcaster();

    private Set<KcApiSocket> sockets = new CopyOnWriteArraySet<>();

    public static KcApiBroadcaster getInstance() {
        return INSTANCE;
    }

    void addSocket(KcApiSocket socket) {
        this.sockets.add(socket);
        LoggerHolder.LOG.info("WebSocket opened");
    }

    void removeSocket(KcApiSocket socket, int statusCode, String reason) {
        this.sockets.remove(socket);
        LoggerHolder.LOG.info("WebSocket closed: [" + String.valueOf(statusCode) + "] " + reason);
    }

    public void broadcast(String txt) {
        this.sockets.forEach(socket -> {
            try {
                socket.send(txt);
            } catch (IOException e) {
                LoggerHolder.LOG.error(e);
            }
        });
    }

    private static class LoggerHolder {
        /**
         * ロガー
         */
        private static final Logger LOG = LogManager.getLogger(LoggerHolder.class);
    }
}
