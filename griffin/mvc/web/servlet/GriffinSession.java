package griffin.mvc.web.servlet;

import java.util.HashMap;
import java.util.Map;

public class GriffinSession {
    private Map<String, Object> attributes = new HashMap<>();

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
