package hazelcastsession;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
        out.println("<html><body>");
        out.println("<h2>Simple Login</h2>");
        
        if (req.getParameter("error") != null) {
            out.println("<p style='color:red'>Please enter a username!</p>");
        }
        
        out.println("<form method='post'>");
        out.println("Username: <input type='text' name='username' required>");
        out.println("<input type='submit' value='Login'>");
        out.println("</form>");
        
        out.println("<hr>");
        out.println("<p><small>Enter any username to test distributed sessions</small></p>");
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String username = req.getParameter("username");
        if (username != null && !username.trim().isEmpty()) {
            // Create session and store username
            HttpSession session = req.getSession(true);
            session.setAttribute("username", username);
            storeUsername(session.getId(), username);
            resp.sendRedirect("/home");
        } else {
            resp.sendRedirect("/login?error=1");
        }
    }
    
    private void storeUsername(String sessionId, String username) {
        JettyHazelcastServer.getUserSessions().put(sessionId, username);
        System.out.println("User '" + username + "' logged in (Session: " + sessionId + ")");
    }
}