package griffin.mvc.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getAnnotatedClasses(List<Class<?>> classes, Class<? extends Annotation> annotation) throws Exception {
        List<String> ret = new ArrayList<>();
        for(Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(annotation)) {
                ret.add(clazz.getName());
                // throw new Exception("PRESENT");
            }
        }
        return ret;
    }

}
