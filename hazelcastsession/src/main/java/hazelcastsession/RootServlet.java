package hazelcastsession;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RootServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        String username = getStoredUsername(session);
        
        if (username != null) {
            resp.sendRedirect("/home");
        } else {
            resp.sendRedirect("/login");
        }
    }
    
    private String getStoredUsername(HttpSession session) {
        if (session == null) return null;
        return JettyHazelcastServer.getUserSessions().get(session.getId());
    }
}