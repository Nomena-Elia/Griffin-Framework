package griffin.mvc.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import griffin.mvc.annotation.Controller;
import griffin.mvc.annotation.UrlMapping;
import griffin.mvc.config.AppProperties;
import griffin.mvc.utils.Mapping;
import griffin.mvc.utils.UrlMethod;
import griffin.mvc.utils.Utils;
import griffin.mvc.web.servlet.GriffinSession;
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
                AppProperties.loadProperties();
                Map<String, Object> controllerInstances = new HashMap<>();
                List<Class<?>> classes = Utils.scanPackage(packageName);
                Map<UrlMethod, Mapping> urlMapping = new HashMap<>();
                Utils.getUrlMapping(classes, Controller.class, urlMapping, controllerInstances);
                context.setAttribute("urlMappings", urlMapping);
                ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
                context.setAttribute("springContainer", ctx);
                Map<String, GriffinSession> sessions = new HashMap<>();
                context.setAttribute("sessions", sessions);
                context.setAttribute("controllerInstances", controllerInstances);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } catch (Exception e) {
            System.out.println("--------------------------------");
            System.out.println(e.getMessage());
            System.out.println("---------------------------------");
            throw new RuntimeException(e);
        }
    }

}
