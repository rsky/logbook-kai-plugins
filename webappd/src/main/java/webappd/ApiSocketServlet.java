package webappd;

import org.eclipse.jetty.websocket.common.extensions.compress.PerMessageDeflateExtension;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ApiSocketServlet extends WebSocketServlet {
    private static final long serialVersionUID = -32739052844288034L;

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getExtensionFactory().register("permessage-deflate", PerMessageDeflateExtension.class);
        factory.register(Socket.class);
    }
}
