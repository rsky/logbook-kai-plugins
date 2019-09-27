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
    private final static int DEFAULT_PORT = 10080;

    public static void main(String[] args) {
        Path cwd = Paths.get(System.getProperty("user.dir"));
        int port = (args.length > 0) ? Integer.parseUnsignedInt(args[0], 10) : DEFAULT_PORT;
        Server server = configureServer(cwd, port);
        try {
            server.start();
            Runtime.getRuntime().addShutdownHook(makeShutdownHook(server));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Server configureServer(Path baseDir, int port) {
        // WebApp resource handler
        ResourceHandler webappHandler = new ResourceHandler();
        webappHandler.setResourceBase(baseDir.resolve("./webapp").toString());

        // KanColle resource handler
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(baseDir.resolve("./resources").toString());
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

        return server;
    }

    private static Thread makeShutdownHook(final Server server) {
        return new Thread(() -> {
            try {
                server.stop();
            } catch (Exception e) {
                System.err.println("An error occurred while stopping the server.");
                e.printStackTrace();
            }
        });
    }
}
