package griffin.mvc.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationContext;

import griffin.mvc.annotation.UrlMapping;
import griffin.mvc.exception.DuplicateUrlException;
import jakarta.servlet.ServletContext;

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

    public static void getUrlMapping(List<Class<?>> classes, Class<? extends Annotation> annotation, Map<UrlMethod, Mapping> urlMappings) throws Exception {
        for(Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(annotation)) {
                List<Method> annotatedMethod = getAnnotatedMethod(clazz, UrlMapping.class);
                for(Method m : annotatedMethod) {
                    UrlMapping urlAnnotation = m.getAnnotation(UrlMapping.class);
                    String url = urlAnnotation.url();
                    Mapping map = new Mapping(clazz, m);
                    UrlMethod method = new UrlMethod();
                    method.setUrl(url);
                    method.setMethod(urlAnnotation.method());
                    if(urlMappings.containsKey(method)) {
                        DuplicateUrlException ex = new DuplicateUrlException();
                        ex.setExisting(urlMappings.get(method));
                        ex.setIntended(map);
                        ex.setUrl(method);
                        throw ex;
                    }
                    urlMappings.put(method, map);
                }
                // throw new Exception("PRESENT");
            }
        }
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

    private static int findContextIndex(Class<?>[] parameterTypes) {
        if(parameterTypes == null) {
            return -1;
        }
        return IntStream.range(0, parameterTypes.length)
        .filter(i -> parameterTypes[i] == ApplicationContext.class)
        .findFirst()
        .orElse(-1);
    }

    public static Object invokeMapping(Mapping mapping, ServletContext context) throws Exception {
        Method method = mapping.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        int contextIndex = findContextIndex(parameterTypes);
        Object[] parameters = new Object[parameterTypes.length];
        if(contextIndex != -1) {
            parameters[contextIndex] = context.getAttribute("springContainer");
        }
        Object invoking = mapping.getController().getConstructor().newInstance();
        return mapping.getMethod().invoke(invoking, parameters);
    }

}