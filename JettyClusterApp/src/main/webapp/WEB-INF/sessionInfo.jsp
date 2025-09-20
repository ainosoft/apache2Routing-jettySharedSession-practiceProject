<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Jetty Cluster Session Test</title>
</head>
<body>
    <h1>Jetty Cluster Session Test</h1>
    <h2>Server: ${serverName}</h2>
    <p><strong>Session ID:</strong> ${sessionId}</p>
    <p><strong>Visit Count:</strong> ${visitCount}</p>
    <p><strong>Created Time:</strong> ${createdTime}</p>
    <p><strong>Last Visit Time:</strong> ${lastVisitTime}</p>
    <p><strong>Max Inactive Interval:</strong> ${maxInactiveInterval} seconds</p>
    <p><a href="<%= request.getRequestURI() %>">Refresh</a></p>
</body>
</html>
