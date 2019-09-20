package webappd

import java.util.concurrent.CopyOnWriteArraySet

internal class Broadcaster {

    private val sockets = CopyOnWriteArraySet<Socket>()

    fun addSocket(socket: Socket) {
        this.sockets.add(socket)
    }

    fun removeSocket(socket: Socket) {
        this.sockets.remove(socket)
    }

    fun broadcast(txt: String) {
        this.sockets.forEach { socket -> socket.send(txt) }
    }

    companion object {
        val instance = Broadcaster()
    }
}
