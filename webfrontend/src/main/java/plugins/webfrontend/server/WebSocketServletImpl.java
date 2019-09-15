package plugins.webfrontend.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebSocketServletImpl extends WebSocketServlet {
    private static final long serialVersionUID = -32739052844288034L;

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(KcApiSocket.class);
    }
}
