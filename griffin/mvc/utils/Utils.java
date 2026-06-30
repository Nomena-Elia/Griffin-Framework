package griffin.mvc.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import griffin.mvc.annotation.UrlMapping;
import griffin.mvc.exception.DuplicateUrlException;

public class Utils {
    public static List<Class<?>> scanPackage(String packageName) throws Exception {
        List<Class<?>> ret = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String formatedName = packageName.replaceAll("\\.", "/");
        URL ressource = loader.getResource(formatedName);
        if(ressource == null) {
            throw new Exception("Package not found: " + packageName);
        }
        String path = ressource.getPath().replaceAll("%20", " ");
        // throw new Exception(path);
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(File f : files) {
                String fileName = f.getName();
                if(fileName.endsWith(".class")) {
                    String className = fileName.split("\\.")[0];
                    String fullClassName = packageName + "." + className;
                    Class<?> clazz = Class.forName(fullClassName);
                    ret.add(clazz);
                }
            }
        }
        return ret;
    }

    public static List<Class<?>> getAnnotatedClasses(List<Class<?>> classes, Class<? extends Annotation> annotation) throws Exception {
        List<Class<?>> ret = new ArrayList<>();
        for(Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(annotation)) {
                ret.add(clazz);
                // throw new Exception("PRESENT");
            }
        }
        return ret;
    }

    public static List<Method> getAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> ret = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for(Method m : methods) {
            if(m.isAnnotationPresent(annotation)) {
                ret.add(m);
            }
        }
        return ret;
    }    

    public static Map<UrlMethod, Mapping> getUrlMapping(List<Class<?>> classes) throws DuplicateUrlException {
        Map<UrlMethod, Mapping> ret = new HashMap<>();
        for(Class<?> clazz : classes) {
            List<Method> annotatedMethod = Utils.getAnnotatedMethod(clazz, UrlMapping.class);
            for(Method m : annotatedMethod) {
                UrlMapping annotation = m.getAnnotation(UrlMapping.class);
                String url = annotation.url();
                Mapping map = new Mapping(clazz, m);
                UrlMethod method = new UrlMethod();
                method.setUrl(url);
                method.setMethod(annotation.method());
                if(ret.containsKey(method)) {
                    DuplicateUrlException ex = new DuplicateUrlException();
                    ex.setExisting(ret.get(method));
                    ex.setIntended(map);
                    ex.setUrl(method);
                    throw ex;
                }
                ret.put(method, map);
            }
        }
        return ret;
    }

    public static Object invokeMapping(Mapping mapping) throws Exception {
        Object invoking = mapping.getController().getConstructor().newInstance();
        return mapping.getMethod().invoke(invoking);
    }

}