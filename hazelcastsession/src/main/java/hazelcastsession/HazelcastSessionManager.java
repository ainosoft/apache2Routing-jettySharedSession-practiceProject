package hazelcastsession;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.SessionData;

import java.util.Set;

public class HazelcastSessionManager extends AbstractSessionDataStore {
    
    private final HazelcastInstance hazelcastInstance;
    private final String mapName;
    private IMap<String, SessionData> sessionMap;
    
    public HazelcastSessionManager(HazelcastInstance hazelcastInstance, String mapName) {
        this.hazelcastInstance = hazelcastInstance;
        this.mapName = mapName;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.sessionMap = hazelcastInstance.getMap(mapName);
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this.sessionMap = null;
    }
    
    @Override
    public SessionData doLoad(String id) throws Exception {
        return sessionMap.get(id);
    }
    
    @Override
    public boolean delete(String id) throws Exception {
        return sessionMap.remove(id) != null;
    }
    
    @Override
    public void doStore(String id, SessionData data, long lastSaveTime) throws Exception {
        sessionMap.put(id, data);
    }
    
    @Override
    public Set<String> doGetExpired(Set<String> candidates) {
        
        return candidates;
    }
    
    @Override
    public boolean isPassivating() {
        return true;
    }
    
    @Override
    public boolean exists(String id) throws Exception {
        return sessionMap.containsKey(id);
    }
}
