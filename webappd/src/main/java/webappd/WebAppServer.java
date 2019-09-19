package webappd;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
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
        ResourceHandler webappHandler = new ResourceHandler();
        webappHandler.setResourceBase(cwd.resolve("./webapp").toString());

        // KanColle resource handler
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(cwd.resolve("./resources").toString());
        ContextHandler resourceContextHandler = new ContextHandler();
        resourceContextHandler.setContextPath("/resources");
        resourceContextHandler.setHandler(resourceHandler);

        // Servlet handler
        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");
        servletHandler.addServlet(new ServletHolder(ApiServlet.class), "/pub");
        servletHandler.addServlet(new ServletHolder(ApiSocketServlet.class), "/sub");

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{webappHandler, resourceContextHandler, servletHandler});

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setIncludedMethods("GET", "POST");
        gzipHandler.setIncludedMimeTypes("application/javascript", "application/json");
        gzipHandler.setInflateBufferSize(8192);
        gzipHandler.setMinGzipSize(1024);
        gzipHandler.setHandler(handlers);

        Server server = new Server(port);
        server.setHandler(gzipHandler);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
