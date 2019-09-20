package webappd

import org.eclipse.jetty.websocket.common.extensions.compress.PerMessageDeflateExtension
import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory

class ApiSocketServlet : WebSocketServlet() {

    override fun configure(factory: WebSocketServletFactory) {
        factory.extensionFactory.register("permessage-deflate", PerMessageDeflateExtension::class.java)
        factory.register(Socket::class.java)
    }

    companion object {
        private const val serialVersionUID = -32739052844288034L
    }
}
