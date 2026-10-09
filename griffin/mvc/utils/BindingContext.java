package griffin.mvc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import griffin.mvc.utils.BindingTree.BindingNode;

public class BindingContext {
    // Une Map qui associe le nom de l'objet racine à son arbre de binding
    // personnalisé
    private final Map<String, BindingTree.BindingNode> roots = new HashMap<>();

    public Map<String, BindingTree.BindingNode> getRoots() {
        return roots;
    }

    private final Map<String, Class<?>> rootTypes = new HashMap<>();

    private final Map<String, Type> rootGenericType = new HashMap<>();

    public void registerRootType(String rootName, Class<?> clazz, Type genericType) {
        rootTypes.put(rootName, clazz);
        rootGenericType.put(rootName, genericType);
    }

    public void insertFormValue(String fullPath, String[] value) throws NoSuchFieldException {
        String[] parts = fullPath.split("\\.", 2);
        String rootName = parts[0];

        if (!roots.containsKey(rootName)) {
            Class<?> rootClass = rootTypes.get(rootName);
            roots.put(rootName, new BindingTree.BindingNode(rootName, rootClass, rootGenericType.get(rootName)));
        }

        BindingTree.BindingNode rootNode = roots.get(rootName);

        if (parts.length > 1) {
            insertPathInNode(rootNode, parts[1], value);
        } else {
            rootNode.value = value;
        }
    }

    private void insertPathInNode(BindingTree.BindingNode currentNode, String remainingPath, String[] value)
            throws NoSuchFieldException {
        String[] parts = remainingPath.split("\\.");
        Class<?> currentClass = currentNode.type;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            Class<?> fieldType = null;
            Field found = null;

            if (currentClass != null) {
                try {
                    found = currentClass.getDeclaredField(part);
                    fieldType = found.getType();
                } catch (NoSuchFieldException e) {
                    throw e;
                }
            }

            currentNode.children.putIfAbsent(part,
                    new BindingTree.BindingNode(part, fieldType, found.getGenericType()));
            currentNode = currentNode.children.get(part);

            if (i == parts.length - 1) {
                currentNode.value = value;
            }
            currentClass = fieldType;
        }
    }

    public Object instantiateTree(BindingNode node) throws Exception {
        if (node.children.isEmpty()) {
            return TypeConverter.convert(node.value, node.type, node.genericType);
        }
        Object instance = node.type.getDeclaredConstructor().newInstance();
        for (BindingNode child : node.children.values()) {
            Object childValue = instantiateTree(child);
            Field field = node.type.getDeclaredField(child.propertyName);
            String propertyName = child.propertyName;
            String setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Method setter = node.type.getDeclaredMethod(setterName, field.getType());
            setter.setAccessible(true);
            setter.invoke(instance, childValue);
        }

        return instance;
    }

    public Map<String, Object> instantiateAll() throws Exception {
        Map<String, Object> instances = new HashMap<>();
        for (Map.Entry<String, BindingTree.BindingNode> entry : roots.entrySet()) {
            Object instance = instantiateTree(entry.getValue());
            instances.put(entry.getKey(), instance);
        }
        return instances;
    }
}
