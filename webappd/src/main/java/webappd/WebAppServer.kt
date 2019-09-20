package webappd

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import java.nio.file.Paths

object WebAppServer {
    @JvmStatic
    fun main(args: Array<String>) {
        var port = 10080
        if (args.isNotEmpty()) {
            port = Integer.parseUnsignedInt(args[0], 10)
        }

        val cwd = Paths.get(System.getProperty("user.dir"))

        // WebApp resource handler
        val webappHandler = ResourceHandler()
        webappHandler.resourceBase = cwd.resolve("./webapp").toString()

        // KanColle resource handler
        val resourceHandler = ResourceHandler()
        resourceHandler.resourceBase = cwd.resolve("./resources").toString()
        val resourceContextHandler = ContextHandler()
        resourceContextHandler.contextPath = "/resources"
        resourceContextHandler.handler = resourceHandler

        // Servlet handler
        val servletHandler = ServletContextHandler(ServletContextHandler.SESSIONS)
        servletHandler.contextPath = "/"
        servletHandler.addServlet(ServletHolder(ApiServlet::class.java), "/pub")
        servletHandler.addServlet(ServletHolder(ApiSocketServlet::class.java), "/sub")

        val handlers = HandlerCollection()
        handlers.handlers = arrayOf<Handler>(webappHandler, resourceContextHandler, servletHandler)

        val gzipHandler = GzipHandler()
        gzipHandler.setIncludedMethods("GET", "POST")
        gzipHandler.setIncludedMimeTypes("application/javascript", "application/json")
        gzipHandler.inflateBufferSize = 8192
        gzipHandler.minGzipSize = 1024
        gzipHandler.handler = handlers

        val server = Server(port)
        server.handler = gzipHandler

        try {
            server.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
