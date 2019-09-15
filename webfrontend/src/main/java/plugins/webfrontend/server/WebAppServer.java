package plugins.webfrontend.server;

import logbook.plugin.lifecycle.StartUp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import plugins.webfrontend.bean.WebFrontendConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebAppServer implements StartUp {
    @Override
    public void run() {
        try {
            WebFrontendConfig config = WebFrontendConfig.get();
            Path cwd = Paths.get(System.getProperty("user.dir"));

            // WebApp resource handler
            ResourceHandler webAppResourceHandler = new ResourceHandler();
            webAppResourceHandler.setResourceBase(cwd.resolve("./webapp").toString());

            // KanColle resource handler
            ResourceHandler kcResourceHandler = new ResourceHandler();
            kcResourceHandler.setResourceBase(cwd.resolve("./resources").toString());

            // WebSocket handler
            WebSocketServletImpl webSocketServlet = new WebSocketServletImpl();
            ServletContextHandler webSocketContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            webSocketContextHandler.addServlet(new ServletHolder(webSocketServlet), "/kcapi");

            HandlerCollection handlers = new HandlerCollection();
            handlers.setHandlers(new Handler[]{kcResourceHandler, webAppResourceHandler, webSocketContextHandler});

            Server server = new Server(config.getHttpPort());
            server.setHandler(handlers);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
