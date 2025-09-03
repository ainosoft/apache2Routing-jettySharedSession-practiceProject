package com.example;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class SessionTestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();
        
        // Get or set visit count
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) {
            visitCount = 1;
            session.setAttribute("createdTime", new Date());
        } else {
            visitCount++;
        }
        session.setAttribute("visitCount", visitCount);
        session.setAttribute("lastVisitTime", new Date());
        
        // Get server info
        String serverName = System.getenv("WORKER_NAME");
        if (serverName == null) {
            serverName = "unknown";
        }
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Jetty Cluster Session Test</title></head><body>");
        out.println("<h1>Jetty Cluster Session Test</h1>");
        out.println("<h2>Server: " + serverName + "</h2>");
        out.println("<p><strong>Session ID:</strong> " + sessionId + "</p>");
        out.println("<p><strong>Visit Count:</strong> " + visitCount + "</p>");
        out.println("<p><strong>Created Time:</strong> " + session.getAttribute("createdTime") + "</p>");
        out.println("<p><strong>Last Visit Time:</strong> " + session.getAttribute("lastVisitTime") + "</p>");
        out.println("<p><strong>Max Inactive Interval:</strong> " + session.getMaxInactiveInterval() + " seconds</p>");
        out.println("<p><a href=\"" + request.getRequestURI() + "\">Refresh</a></p>");
        out.println("</body></html>");
    }
}
