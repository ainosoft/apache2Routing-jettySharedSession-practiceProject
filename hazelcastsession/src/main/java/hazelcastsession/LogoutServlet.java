package hazelcastsession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String username = (String) session.getAttribute("username");
            String sessionId = session.getId();
            
            // Clean up maps
            JettyHazelcastServer.getUserSessions().remove(sessionId);
            JettyHazelcastServer.getUserCounters().remove(sessionId);
            
            session.invalidate();
            
            System.out.println("User logged out: " + username + " (Session: " + sessionId + ")");
        }
        
        response.sendRedirect("/login");
    }
}