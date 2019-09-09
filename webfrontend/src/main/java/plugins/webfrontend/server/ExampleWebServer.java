package plugins.webfrontend.server;

import logbook.plugin.lifecycle.StartUp;
import org.eclipse.jetty.server.Server;

public class ExampleWebServer implements StartUp {
    @Override
    public void run() {
        try {
            Server server = new Server(7777);
            server.setHandler(new ExampleHandler());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
