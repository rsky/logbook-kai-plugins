package plugins.webfrontend.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexHandler extends AbstractHandler {
    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
         if (s.equals("/index.html")) {
             httpServletResponse.setContentType("text/html;charset=utf-8");
             httpServletResponse.setStatus(HttpServletResponse.SC_OK);
             request.setHandled(true);
             httpServletResponse.getWriter().println("<h1>Hello Jetty!!</h1>");
         }
    }
}
