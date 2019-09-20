package webappd

import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.WebSocket

import java.io.IOException

@WebSocket
class Socket {
    private var session: Session? = null
    private var remoteHost: String? = null

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        this.session = session
        this.remoteHost = session.remoteAddress.hostString
        Broadcaster.instance.addSocket(this)

        // On connect, then send the latest start-and-port data.
        val holder = DataHolder.instance
        val startJSON = holder.startJSON
        if (startJSON != null) {
            this.send(startJSON)
        }
        val portJSON = holder.portJSON
        if (portJSON != null) {
            this.send(portJSON)
        }

        println("WebSocket connected: " + this.remoteHost!!)
    }

    @OnWebSocketClose
    fun onClose(statusCode: Int, reason: String) {
        Broadcaster.instance.removeSocket(this)

        println("WebSocket closed: " + this.remoteHost + " [" + statusCode + "] " + reason)
    }

    internal fun send(txt: String) {
        if (this.session!!.isOpen) {
            try {
                this.session!!.remote.sendString(txt)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
