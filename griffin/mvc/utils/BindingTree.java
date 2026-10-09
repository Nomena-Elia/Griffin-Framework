package griffin.mvc.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BindingTree {
    private final BindingNode root = new BindingNode("root");

    public static class BindingNode {
        String propertyName;
        String[] value; 
        Class<?> type;
        Type genericType;
        Map<String, BindingNode> children = new HashMap<>();

        BindingNode(String propertyName) {
            this.propertyName = propertyName;
        }

        BindingNode(String propertyName, Class<?> type) {
            this.propertyName = propertyName;
            this.type = type;
        }

        BindingNode(String propertyName, Class<?> type, Type genericType) {
            this.propertyName = propertyName;
            this.type = type;
            this.genericType = genericType;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("PROPERTY: ").append(propertyName).append("\n");
            sb.append("CHILDREN: \n").append(children);
            return sb.toString();
        }
    }

    public BindingNode getRoot() { return root; }

    public void insertPath(String path, String[] value) {
        String[] parts = path.split("\\."); 
        BindingNode current = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            current.children.putIfAbsent(part, new BindingNode(part));
            current = current.children.get(part);
            if (i == parts.length - 1) {
                current.value = value;
            }
        }
    }
}
