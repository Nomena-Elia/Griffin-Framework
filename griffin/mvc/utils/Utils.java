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
        URL ressource = loader.getResource(packageName);
        if(ressource == null) {
            throw new Exception("Package not found: " + packageName);
        }
        return ret;
    }

    public static List<String> getAnnotatedClasses(List<Class<?>> classes, Class<? extends Annotation> annotation) {
        List<String> ret = new ArrayList<>();
        for(Class<?> clazz : classes) {
            if(clazz.isAnnotationPresent(annotation)) {
                ret.add(clazz.getName());
            }
        }
        return ret;
    }

}
