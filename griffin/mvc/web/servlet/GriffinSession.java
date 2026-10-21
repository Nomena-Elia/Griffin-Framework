package griffin.mvc.web.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GriffinSession {
    private Map<String, Object> attributes = new HashMap<>();
    private long creationTime;
    private long lastAccessedTime;
    private long maxInactiveInterval;

    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public Set<String> getAttributesName() {
        return attributes.keySet();
    } 

    public void setMaxInactiveInterval(long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public void set(String key, Object value) {
        attributes.put(key, value);
    }

    public void remove(String key) {
        attributes.remove(key);
    }

    public Object get(String key) {
        return attributes.get(key);
    }
}
