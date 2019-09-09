package plugins.webfrontend.server;

import logbook.plugin.lifecycle.StartUp;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.nio.file.Paths;

public class ExampleWebServer implements StartUp {
    @Override
    public void run() {
        try {
            Server server = new Server(7777);

            ResourceHandler resourceHandler = new ResourceHandler();
            String resourcesDir = Paths.get(System.getProperty("user.dir")).resolve("./resources").toString();
            resourceHandler.setResourceBase(resourcesDir);
            HandlerList handlerList = new HandlerList();
            handlerList.addHandler(resourceHandler); // first!
            handlerList.addHandler(new IndexHandler());

            server.setHandler(handlerList);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
