package webappd

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet
class ApiServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val reader = req.reader
        val sb = StringBuilder()
        val buf = CharArray(8192)
        var read = 0
        while ({read = reader.read(buf); read}() != -1) {
            sb.append(buf, 0, read)
        }
        val jsonStr = sb.toString()

        val apiURI = req.getHeader("X-API-URI")
        if (apiURI != null) {
            when (apiURI) {
                DataHolder.PORT_URI -> DataHolder.instance.portJSON = jsonStr
                DataHolder.START_URI -> DataHolder.instance.startJSON = jsonStr
            }
        }

        Broadcaster.instance.broadcast(jsonStr)

        resp.status = 200
        resp.contentType = "text/plain"
        resp.writer.println("OK")
    }

    companion object {
        private const val serialVersionUID = -3156766023320514316L
    }
}
