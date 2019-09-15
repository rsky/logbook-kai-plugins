package webappd;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class Broadcaster {
    private static final Broadcaster INSTANCE = new Broadcaster();

    private Set<Socket> sockets = new CopyOnWriteArraySet<>();

    static Broadcaster getInstance() {
        return INSTANCE;
    }

    void addSocket(Socket socket) {
        System.out.println("WebSocket opened");
        this.sockets.add(socket);
    }

    void removeSocket(Socket socket, int statusCode, String reason) {
        System.out.println("WebSocket closed: [" + String.valueOf(statusCode) + "] " + reason);
        this.sockets.remove(socket);
    }

    void broadcast(String txt) {
        //System.out.println("broadcast: " + txt);
        this.sockets.forEach(socket -> {
            try {
                socket.send(txt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
