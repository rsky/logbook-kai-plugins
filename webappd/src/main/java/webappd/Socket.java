package webappd;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class Socket {
    private Session session;
    private String remoteHost;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        this.remoteHost = session.getRemoteAddress().getHostString();
        Broadcaster.getInstance().addSocket(this);

        // On connect, then send the latest start-and-port data.
        DataHolder holder = DataHolder.getInstance();
        String startJSON = holder.getStartJSON();
        if (startJSON != null) {
            this.send(startJSON);
        }
        String portJSON = holder.getPortJSON();
        if (portJSON != null) {
            this.send(portJSON);
        }

        System.out.println("WebSocket connected: " + this.remoteHost);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        Broadcaster.getInstance().removeSocket(this);

        System.out.println("WebSocket closed: " + this.remoteHost + " [" + statusCode + "] " + reason);
    }

    void send(String txt) {
        if (this.session.isOpen()) {
            try {
                this.session.getRemote().sendString(txt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void goAway() {
        this.session.close(1001, "The server is going away.");
    }
}
