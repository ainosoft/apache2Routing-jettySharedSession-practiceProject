package com.example;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.Date;

public class SessionTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(true);
        String sessionId = session.getId();

        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) {
            visitCount = 1;
            session.setAttribute("createdTime", new Date());
        } else {
            visitCount++;
        }
        session.setAttribute("visitCount", visitCount);
        session.setAttribute("lastVisitTime", new Date());

        String serverName = System.getenv("WORKER_NAME");
        if (serverName == null) {
            serverName = "unknown";
        }

       
        request.setAttribute("sessionId", sessionId);
        request.setAttribute("visitCount", visitCount);
        request.setAttribute("createdTime", session.getAttribute("createdTime"));
        request.setAttribute("lastVisitTime", session.getAttribute("lastVisitTime"));
        request.setAttribute("maxInactiveInterval", session.getMaxInactiveInterval());
        request.setAttribute("serverName", serverName);

        try {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/sessionInfo.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
