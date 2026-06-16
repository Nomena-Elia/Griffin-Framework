package griffin.mvc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import griffin.mvc.annotation.Controller;
import griffin.mvc.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Nomena
 */
public class FrontControllerServlet extends HttpServlet {

    private List<String> listControllers;

    @Override
    public void init() throws ServletException {
        String packageName = this.getInitParameter("packageName");
        try {
            List<Class<?>> classes = Utils.scanPackage(packageName);
            listControllers = Utils.getAnnotatedClasses(classes, Controller.class);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURL().toString();
        try (PrintWriter out = res.getWriter()) {
            for (String className : listControllers) {
                out.println(className);
            }
            out.flush();
        }
    }
}
