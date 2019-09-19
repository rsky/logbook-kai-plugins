package webappd;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet
public class ApiServlet extends HttpServlet {
    private static final long serialVersionUID = -3156766023320514316L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        char[] cbuf = new char[8192];
        int read;
        while ((read = reader.read(cbuf)) != -1) {
            sb.append(cbuf, 0, read);
        }
        String jsonStr = sb.toString();

        String apiURI = req.getHeader("X-API-URI");
        if (apiURI != null) {
            switch (apiURI) {
                case DataHolder.PORT_URI:
                    DataHolder.getInstance().setPortJSON(jsonStr);
                    break;
                case DataHolder.START_URI:
                    DataHolder.getInstance().setStartJSON(jsonStr);
                    break;
            }
        }

        Broadcaster.getInstance().broadcast(jsonStr);

        resp.setStatus(200);
        resp.setContentType("text/plain");
        resp.getWriter().println("OK");
    }
}
