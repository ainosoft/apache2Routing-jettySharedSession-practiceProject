package hazelcastsession;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HomeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        String username = getStoredUsername(session);
        
        if (username == null) {
            resp.sendRedirect("/login");
            return;
        }
        
        
        String sessionId = session.getId();
        Integer counter = JettyHazelcastServer.getUserCounters().get(sessionId);
        counter = (counter == null) ? 1 : counter + 1;
        JettyHazelcastServer.getUserCounters().put(sessionId, counter);
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        out.println("<html><body>");
        out.println("<h2>🏠 Welcome " + username + "!</h2>");
        
        out.println("<div style='background:#f0f0f0; padding:15px; margin:10px 0;'>");
        out.println("<h3>Session Info:</h3>");
        out.println("<p><strong>Session ID:</strong> " + sessionId + "</p>");
        out.println("<p><strong>Server Port:</strong> " + req.getServerPort() + "</p>");
        out.println("<p><strong>Visit Count:</strong> " + counter + "</p>");
        if (JettyHazelcastServer.getHazelcast() != null) {
            out.println("<p><strong>Cluster Size:</strong> " + JettyHazelcastServer.getHazelcast().getCluster().getMembers().size() + " servers</p>");
        }
        out.println("</div>");
        
        out.println("<h3>Test Session Sharing:</h3>");
        out.println("<ol>");
        out.println("<li>Keep this page open</li>");
        out.println("<li>Start another server: <code>java -cp target/jetty-hazelcast-session-1.0-SNAPSHOT.jar:target/lib/* hazelcastsession.JettyHazelcastServer 8081</code></li>");
        out.println("<li>Visit: <a href='http://localhost:8081/home' target='_blank'>http://localhost:8081/home</a></li>");
        out.println("<li>You should see the same session data! ✅</li>");
        out.println("</ol>");
        
        out.println("<div style='margin:20px 0;'>");
        out.println("<a href='/home'>Refresh</a> | ");
        out.println("<a href='/logout'>Logout</a> | ");
        out.println("<a href='/info'> Session Info</a>");
        out.println("</div>");
        
        out.println("</body></html>");
    }
    
    private String getStoredUsername(HttpSession session) {
        if (session == null) return null;
        // Try session attribute first, then fallback to map
        String username = (String) session.getAttribute("username");
        if (username == null) {
            username = JettyHazelcastServer.getUserSessions().get(session.getId());
        }
        return username;
    }
}