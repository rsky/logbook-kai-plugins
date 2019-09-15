package webappd;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebAppServer {
    public static void main(String[] args) {
        int port = 10080;

        if (args.length > 0) {
            port = Integer.parseUnsignedInt(args[0], 10);
        }

        Path cwd = Paths.get(System.getProperty("user.dir"));

        // WebApp resource handler
        ResourceHandler webAppResourceHandler = new ResourceHandler();
        webAppResourceHandler.setResourceBase(cwd.resolve("./webapp").toString());

        // KanColle resource handler
        ResourceHandler kcResourceHandler = new ResourceHandler();
        kcResourceHandler.setResourceBase(cwd.resolve("./resources").toString());

        // Servlet handler
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{kcResourceHandler, webAppResourceHandler, servletContextHandler});

        Server server = new Server(port);
        server.setHandler(handlers);

        servletContextHandler.addServlet(new ServletHolder(ApiServlet.class), "/api");
        servletContextHandler.addServlet(new ServletHolder(ApiSocketServlet.class), "/socket");

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
