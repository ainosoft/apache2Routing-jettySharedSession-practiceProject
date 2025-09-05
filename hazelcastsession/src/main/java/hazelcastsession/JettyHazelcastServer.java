package hazelcastsession;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.FileNotFoundException;

public class JettyHazelcastServer {

	private static final String DEFAULT_PORT = "8080";
	private static final String DEFAULT_NODE_NAME = "node1";

	private static HazelcastInstance hazelcastInstance;
	private static IMap<String, String> userSessions;
	private static IMap<String, Integer> userCounters;

	public static void main(String[] args) throws Exception {

		String portArg = args.length > 0 ? args[0] : DEFAULT_PORT;
		int port = Integer.parseInt(portArg);
		String nodeName = System.getProperty("node.name", DEFAULT_NODE_NAME + "-" + port);

		System.out.println("Starting Jetty server on port " + port + " with node name: " + nodeName);

		Server server = new Server(port);

		hazelcastInstance = initializeHazelcast(nodeName);

		userSessions = hazelcastInstance.getMap("user-sessions");
		userCounters = hazelcastInstance.getMap("user-counters");

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		setupSessionManagement(context, hazelcastInstance);

		context.addServlet(new ServletHolder(new LoginServlet()), "/login");
		context.addServlet(new ServletHolder(new HomeServlet()), "/home");
		context.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
		context.addServlet(new ServletHolder(new SessionInfoServlet()), "/info");
		context.addServlet(new ServletHolder(new SessionDemoServlet()), "/demo");
		context.addServlet(new ServletHolder(new RootServlet()), "/");

		server.setHandler(context);

		// Start server
		server.start();
		System.out.println("Server started successfully on port " + port);
		System.out.println("Access the application at: http://localhost:" + port);
		System.out.println("Hazelcast cluster members: " + hazelcastInstance.getCluster().getMembers());

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				System.out.println("Shutting down server...");
				server.stop();
				if (hazelcastInstance != null) {
					hazelcastInstance.shutdown();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		server.join();
	}

	private static HazelcastInstance initializeHazelcast(String nodeName) {
		try {
			Config config = new XmlConfigBuilder("hazelcast.xml").build();
			config.setInstanceName(nodeName);
			return Hazelcast.newHazelcastInstance(config);
		} catch (FileNotFoundException e) {
			System.err.println("hazelcast.xml not found, using default configuration");
			Config config = new Config();
			config.setInstanceName(nodeName);

			config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
			config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);

			config.getMapConfig("user-sessions").setBackupCount(1).setAsyncBackupCount(1).setTimeToLiveSeconds(1800);
			config.getMapConfig("user-counters").setBackupCount(1).setAsyncBackupCount(1);

			config.getMapConfig("jetty-sessions").setBackupCount(1).setAsyncBackupCount(1).setTimeToLiveSeconds(1800);

			return Hazelcast.newHazelcastInstance(config);
		}
	}

	private static void setupSessionManagement(ServletContextHandler context, HazelcastInstance hazelcastInstance) {
		try {
			// Create custom Hazelcast session data store
			HazelcastSessionDataStore sessionDataStore = new HazelcastSessionDataStore(hazelcastInstance);

			// Create session cache
			DefaultSessionCache sessionCache = new DefaultSessionCache(context.getSessionHandler());
			sessionCache.setSessionDataStore(sessionDataStore);

			// Get the session handler from context and configure it
			SessionHandler sessionHandler = context.getSessionHandler();
			sessionHandler.setSessionCache(sessionCache);
			sessionHandler.setMaxInactiveInterval(1800); // 30 minutes
			sessionHandler.setHttpOnly(true); // Security improvement

			System.out.println("Hazelcast session management configured successfully");
		} catch (Exception e) {
			System.err.println("Failed to setup session management: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	public static IMap<String, String> getUserSessions() {
		return userSessions;
	}

	public static IMap<String, Integer> getUserCounters() {
		return userCounters;
	}

	public static HazelcastInstance getHazelcast() {
		return hazelcastInstance;
	}
}