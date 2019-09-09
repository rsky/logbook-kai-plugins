package plugins.webfrontend.server;

import logbook.plugin.lifecycle.StartUp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebAppServer implements StartUp {
    @Override
    public void run() {
        try {
            Path cwd = Paths.get(System.getProperty("user.dir"));

            ResourceHandler webAppRH = new ResourceHandler();
            webAppRH.setResourceBase(cwd.resolve("./webapp").toString());

            ResourceHandler resourcesRH = new ResourceHandler();
            resourcesRH.setResourceBase(cwd.resolve("./resources").toString());

            HandlerCollection handlers = new HandlerCollection();
            handlers.setHandlers(new Handler[]{ resourcesRH, webAppRH });

            Server server = new Server(7777);
            server.setHandler(handlers);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
