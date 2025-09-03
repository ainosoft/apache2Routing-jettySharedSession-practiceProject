package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DatabaseAdaptor;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionDataStore;
import org.eclipse.jetty.server.session.JDBCSessionDataStore.SessionTableSchema;
import org.eclipse.jetty.server.session.JDBCSessionDataStoreFactory;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyServer {
    
    public static void main(String[] args) throws Exception {
        String workerName = System.getenv("WORKER_NAME");
        if (workerName == null) {
            workerName = "node1";
        }
        
        int port = 8080;
        String portStr = System.getenv("SERVER_PORT");
        if (portStr != null) {
            port = Integer.parseInt(portStr);
        }
        
        Server server = new Server(port);
        
        // Configure Session ID Manager for clustering
        configureSessionIdManager(server, workerName);
        
        // Create web application context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // Configure session management for this context
        configureSessionHandler(context);
        
        // Add test servlet
        context.addServlet(new ServletHolder(new SessionTestServlet()), "/*");
        
        server.setHandler(context);
        
        System.out.println("Starting Jetty server on port " + port + " with worker name: " + workerName);
        server.start();
        server.join();
    }
    
    private static void configureSessionIdManager(Server server, String workerName) {
        DefaultSessionIdManager sessionIdManager = new DefaultSessionIdManager(server);
        sessionIdManager.setWorkerName(workerName);
        server.setSessionIdManager(sessionIdManager);
    }
    
    private static void configureSessionHandler(ServletContextHandler context) {
        SessionHandler sessionHandler = new SessionHandler();
        
        // Configure session cache
        DefaultSessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setEvictionPolicy(SessionCache.NEVER_EVICT);
        sessionHandler.setSessionCache(sessionCache);
        
        // Configure JDBC session data store
        JDBCSessionDataStoreFactory storeFactory = new JDBCSessionDataStoreFactory();
        
        // Configure database adapter
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDriverInfo("com.mysql.cj.jdbc.Driver", 
            "jdbc:mysql://mysql:3306/myjettydb?user=root&password=mysql%23hznr&useSSL=false&allowPublicKeyRetrieval=true");
        storeFactory.setDatabaseAdaptor(databaseAdaptor);
        
        // Configure session table schema
        SessionTableSchema schema = new SessionTableSchema();
        schema.setTableName("JettySessions");
        schema.setSessionIdColumn("sessionId");
        schema.setContextPathColumn("contextPath");
        schema.setVirtualHostColumn("virtualHost");
        schema.setLastNodeColumn("lastNode");
        schema.setAccessTimeColumn("accessTime");
        schema.setLastAccessTimeColumn("lastAccessTime");
        schema.setCreateTimeColumn("createTime");
        schema.setCookieTimeColumn("cookieTime");
        schema.setLastSavedTimeColumn("lastSavedTime");
        schema.setExpiryTimeColumn("expiryTime");
        schema.setMaxIntervalColumn("maxInterval");
        schema.setMapColumn("map");
        storeFactory.setSessionTableSchema(schema);
        
        JDBCSessionDataStore dataStore = storeFactory.getSessionDataStore(sessionHandler);
        sessionCache.setSessionDataStore(dataStore);
        
        // Configure session settings
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSecureRequestOnly(false);
        sessionHandler.setMaxInactiveInterval(1800); // 30 minutes
        
        context.setSessionHandler(sessionHandler);
    }
}
