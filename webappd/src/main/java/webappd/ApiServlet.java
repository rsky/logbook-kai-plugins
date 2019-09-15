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
    private static final long serialVersionUID = -7301406034223912164L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        char[] cbuf = new char[8192];
        int read;
        while ((read = reader.read(cbuf)) != -1) {
            sb.append(cbuf, 0, read);
        }
        Broadcaster.getInstance().broadcast(sb.toString());
        resp.setStatus(200);
        resp.setContentType("text/plain");
        resp.getWriter().println("OK");
    }
}
