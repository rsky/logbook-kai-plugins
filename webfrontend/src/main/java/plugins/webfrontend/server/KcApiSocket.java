package plugins.webfrontend.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class KcApiSocket {
    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        KcApiBroadcaster.getInstance().addSocket(this);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        KcApiBroadcaster.getInstance().removeSocket(this, statusCode, reason);
    }

    void send(String txt) throws IOException {
        if (this.session.isOpen()) {
            this.session.getRemote().sendString(txt);
        }
    }
}
