package com.example;

import java.io.IOException;
import java.net.InetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        // Get or initialize a hit count in the session
        Integer hitCount = (Integer) session.getAttribute("hit_count");
        hitCount = (hitCount == null) ? 1 : hitCount + 1;
        session.setAttribute("hit_count", hitCount);

        // Get the container's hostname to see which node is serving the request
        String containerId = "unknown";
        try {
            containerId = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            // Ignore
        }

        // Write the response
        resp.setContentType("text/html");
        resp.getWriter().println("<html><body style='font-family: sans-serif; text-align: center; margin-top: 50px;'>");
        resp.getWriter().println("<h1>Jetty Cluster Test - Fresh Start</h1>");
        resp.getWriter().println("<div style='background-color: #f0f0f0; padding: 20px; border-radius: 8px; display: inline-block;'>");
        resp.getWriter().println("<h3>Served by container: <b style='color: #007bff;'>" + containerId + "</b></h3>");
        resp.getWriter().println("<h3>Session Hit Count: <b style='color: #28a745;'>" + hitCount + "</b></h3>");
        resp.getWriter().println("</div>");
        resp.getWriter().println("<p>Refresh this page to see the hit count increase and the container ID change.</p>");
        resp.getWriter().println("</body></html>");
    }
}
