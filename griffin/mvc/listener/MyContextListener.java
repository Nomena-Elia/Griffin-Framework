package griffin.mvc.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import griffin.mvc.annotation.Controller;
import griffin.mvc.utils.Mapping;
import griffin.mvc.utils.UrlMethod;
import griffin.mvc.utils.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class MyContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        ServletContextListener.super.contextDestroyed(sce);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        ServletContextListener.super.contextInitialized(sce);
        ServletContext context = sce.getServletContext();
        String packageName = context.getInitParameter("controllersPackage");
        // System.out.println("--------------------------------");
        // System.out.println(packageName);
        // System.out.println("--------------------------------");
        try {
            try {
                List<Class<?>> classes = Utils.scanPackage(packageName);
                Map<UrlMethod, Mapping> urlMapping = new HashMap<>();
                Utils.getUrlMapping(classes, Controller.class, urlMapping);
                context.setAttribute("urlMappings", urlMapping);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } catch (Exception e) {
            System.out.println("--------------------------------");
            System.out.println(e.getMessage());
            System.out.println("--------------------------------");
            throw new RuntimeException(e);
        }
    }

}
