package hazelcastsession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Map;

import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.SessionData;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazelcastSessionDataStore extends AbstractSessionDataStore {

    private final HazelcastInstance hazelcastInstance;
    private final IMap<String, SessionDataWrapper> sessionMap;

    public HazelcastSessionDataStore(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.sessionMap = hazelcastInstance.getMap("jetty-sessions");
    }

    @Override
    public boolean isPassivating() {
        return true;
    }

    @Override
    public boolean exists(String id) throws Exception {
        return sessionMap.containsKey(id);
    }

    @Override
    public SessionData doLoad(String id) throws Exception {
        SessionDataWrapper wrapper = sessionMap.get(id);
        if (wrapper != null) {
            SessionData sessionData = new SessionData(id, "", "", 
                wrapper.getCreated(), wrapper.getAccessed(), 
                wrapper.getLastAccessed(), wrapper.getMaxInactiveMs());
            
            // Restore attributes one by one instead of putAll
            if (wrapper.getAttributes() != null) {
                for (Map.Entry<String, Object> entry : wrapper.getAttributes().entrySet()) {
                    sessionData.setAttribute(entry.getKey(), entry.getValue());
                }
            }
            
            System.out.println("Session loaded from Hazelcast: " + id);
            return sessionData;
        }
        return null;
    }

    @Override
    public boolean delete(String id) throws Exception {
        boolean existed = sessionMap.containsKey(id);
        sessionMap.remove(id);
        if (existed) {
            System.out.println(" Session deleted from Hazelcast: " + id);
        }
        return existed;
    }

    @Override
    public void doStore(String id, SessionData data, long lastSaveTime) throws Exception {
        SessionDataWrapper wrapper = new SessionDataWrapper();
        wrapper.setId(id);
        wrapper.setCreated(data.getCreated());
        wrapper.setAccessed(data.getAccessed());
        wrapper.setLastAccessed(data.getLastAccessed());
        wrapper.setMaxInactiveMs(data.getMaxInactiveMs());
        
     
        ConcurrentHashMap<String, Object> attrs = new ConcurrentHashMap<>();
        for (String name : data.getKeys()) {
            attrs.put(name, data.getAttribute(name));
        }
        wrapper.setAttributes(attrs);
        
        sessionMap.put(id, wrapper);
        System.out.println("Session stored in Hazelcast: " + id);
    }

    @Override
    public Set<String> doGetExpired(Set<String> candidates) {
       
        return candidates;
    }

    public static class SessionDataWrapper implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private long created;
        private long accessed;
        private long lastAccessed;
        private long maxInactiveMs;
        private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public long getCreated() { return created; }
        public void setCreated(long created) { this.created = created; }
        
        public long getAccessed() { return accessed; }
        public void setAccessed(long accessed) { this.accessed = accessed; }
        
        public long getLastAccessed() { return lastAccessed; }
        public void setLastAccessed(long lastAccessed) { this.lastAccessed = lastAccessed; }
        
        public long getMaxInactiveMs() { return maxInactiveMs; }
        public void setMaxInactiveMs(long maxInactiveMs) { this.maxInactiveMs = maxInactiveMs; }
        
        public ConcurrentHashMap<String, Object> getAttributes() { return attributes; }
        public void setAttributes(ConcurrentHashMap<String, Object> attributes) { this.attributes = attributes; }
    }
}