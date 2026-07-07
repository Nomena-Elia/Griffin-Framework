package griffin.mvc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import griffin.mvc.exception.UrlNotFoundException;
import griffin.mvc.utils.Mapping;
import griffin.mvc.utils.UrlMethod;
import griffin.mvc.utils.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Nomena
 */
public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, Mapping> urlMappings;

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        urlMappings = (Map<UrlMethod, Mapping>) this.getServletContext().getAttribute("urlMappings");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    /**
     * @author <b>Nomena</b>
     * @param req : The object representation of the request
     * @param res : The object representation of the response
     * @throws ServletException
     * @throws IOException
     */

    public void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String uri = req.getPathInfo();
        UrlMethod method = new UrlMethod();
        method.setUrl(uri);
        method.setMethod(req.getMethod());
        Mapping mapping = urlMappings.get(method);
        if(mapping == null) {
            UrlNotFoundException ex = new UrlNotFoundException(urlMappings, method);
            throw new ServletException(ex);
        }
        
        try (PrintWriter out = res.getWriter();) {
            Object result = Utils.invokeMapping(mapping);
            out.println(result);
            System.out.println(result);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}
