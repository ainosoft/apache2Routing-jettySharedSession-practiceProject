package hazelcastsession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

public class SessionDemoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        HttpSession session = req.getSession(true);
        String sessionId = session.getId();
        String serverInfo = getServerInfo();
        
        // Get session attributes
        String username = (String) session.getAttribute("username");
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        
        if (visitCount == null) {
            visitCount = 0;
        }
        visitCount++;
        session.setAttribute("visitCount", visitCount);
        
        // HTML Response
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Distributed Session Demo</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".info-box { background-color: #f0f0f0; padding: 20px; border-radius: 5px; margin: 10px 0; }");
        out.println(".server-info { background-color: #e6f3ff; }");
        out.println(".session-info { background-color: #f0fff0; }");
        out.println("</style></head><body>");
        
        out.println("<h1>🌐 Distributed Session Store Demo</h1>");
        out.println("<h2>Jetty 9.4 + Hazelcast</h2>");
        
        out.println("<div class='info-box server-info'>");
        out.println("<h3>🖥️ Server Information</h3>");
        out.println("<p><strong>Server:</strong> " + serverInfo + "</p>");
        out.println("<p><strong>Port:</strong> " + req.getServerPort() + "</p>");
        out.println("</div>");
        
        out.println("<div class='info-box session-info'>");
        out.println("<h3>🔐 Session Information</h3>");
        out.println("<p><strong>Session ID:</strong> " + sessionId + "</p>");
        out.println("<p><strong>Visit Count:</strong> " + visitCount + "</p>");
        
        if (username != null) {
            out.println("<p><strong>Logged in as:</strong> " + username + "</p>");
            out.println("<p><a href='/logout'>🚪 Logout</a></p>");
        } else {
            out.println("<p><strong>Status:</strong> Not logged in</p>");
            out.println("<p><a href='/login'>🔑 Login</a></p>");
        }
        out.println("</div>");
        
        out.println("<div class='info-box'>");
        out.println("<h3>🔗 Navigation</h3>");
        out.println("<p><a href='/'>🏠 Home</a> | ");
        out.println("<a href='/info'>ℹ️ Session Details</a></p>");
        out.println("</div>");
        
        out.println("<div class='info-box'>");
        out.println("<h3>🧪 Testing Instructions</h3>");
        out.println("<ol>");
        out.println("<li>Open this page in multiple browser tabs</li>");
        out.println("<li>Login on one tab</li>");
        out.println("<li>Refresh other tabs - you should remain logged in</li>");
        out.println("<li>Access different ports (8080, 8081, 8082) to test cross-node session sharing</li>");
        out.println("</ol>");
        out.println("</div>");
        
        out.println("</body></html>");
    }
    
    private String getServerInfo() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
