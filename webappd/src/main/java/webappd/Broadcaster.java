package webappd;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class Broadcaster {
    private static final Broadcaster INSTANCE = new Broadcaster();

    private Set<Socket> sockets = new CopyOnWriteArraySet<>();

    static Broadcaster getInstance() {
        return INSTANCE;
    }

    void addSocket(Socket socket) {
        this.sockets.add(socket);
    }

    void removeSocket(Socket socket) {
        this.sockets.remove(socket);
    }

    void broadcast(String txt) {
        this.sockets.forEach(socket -> socket.send(txt));
    }
}
