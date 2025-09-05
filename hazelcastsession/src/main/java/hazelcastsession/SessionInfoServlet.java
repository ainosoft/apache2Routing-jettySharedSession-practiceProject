package hazelcastsession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class SessionInfoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        
        out.println("<html><head><title>Session Info - Distributed Session</title></head><body>");
        out.println("<h2>Session Information</h2>");
        
        if (session != null) {
            out.println("<div style='background:#e8f5e8; padding:15px; margin:10px 0; border-radius:5px;'>");
            out.println("<h3>Active Session</h3>");
            out.println("<p><strong>Session ID:</strong> " + session.getId() + "</p>");
            out.println("<p><strong>Creation Time:</strong> " + new java.util.Date(session.getCreationTime()) + "</p>");
            out.println("<p><strong>Last Accessed:</strong> " + new java.util.Date(session.getLastAccessedTime()) + "</p>");
            out.println("<p><strong>Max Inactive Interval:</strong> " + session.getMaxInactiveInterval() + " seconds</p>");
            out.println("<p><strong>Current Server Port:</strong> " + request.getServerPort() + "</p>");
            
            out.println("<h4>📋 Session Attributes:</h4>");
            out.println("<table border='1' style='border-collapse:collapse; width:100%;'>");
            out.println("<tr><th>Attribute Name</th><th>Value</th><th>Type</th></tr>");
            
            Enumeration<String> attributeNames = session.getAttributeNames();
            boolean hasAttributes = false;
            while (attributeNames.hasMoreElements()) {
                hasAttributes = true;
                String name = attributeNames.nextElement();
                Object value = session.getAttribute(name);
                out.println("<tr>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + value + "</td>");
                out.println("<td>" + (value != null ? value.getClass().getSimpleName() : "null") + "</td>");
                out.println("</tr>");
            }
            
            if (!hasAttributes) {
                out.println("<tr><td colspan='3'>No session attributes found</td></tr>");
            }
            
            out.println("</table>");
            out.println("</div>");
            
        } else {
            out.println("<div style='background:#ffe8e8; padding:15px; margin:10px 0; border-radius:5px;'>");
            out.println("<h3>No Active Session</h3>");
            out.println("<p>Please <a href='/login'>login</a> to create a session.</p>");
            out.println("</div>");
        }
        
        out.println("<div style='margin:20px 0;'>");
        out.println("<a href='/login' style='margin-right:10px;'> Login</a>");
        out.println("<a href='/home' style='margin-right:10px;'>Home</a>");
        out.println("<a href='/info'>Refresh</a>");
        out.println("</div>");
        
        out.println("</body></html>");
    }
}