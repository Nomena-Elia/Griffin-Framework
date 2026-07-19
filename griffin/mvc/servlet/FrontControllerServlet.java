package griffin.mvc.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import griffin.mvc.exception.UrlNotFoundException;
import griffin.mvc.utils.Mapping;
import griffin.mvc.utils.UrlMethod;
import griffin.mvc.utils.Utils;
import griffin.mvc.web.servlet.ModelAndView;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Nomena
 */
public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, Mapping> urlMappings;
    private String suffix;
    private String prefix;

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        urlMappings = (Map<UrlMethod, Mapping>) this.getServletContext().getAttribute("urlMappings");
        suffix = (String) this.getServletContext().getInitParameter("suffix");
        prefix = (String) this.getServletContext().getInitParameter("prefix");
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
        if(urlMappings == null) {
            throw new ServletException("NULL");
        }
        String uri = req.getPathInfo();
        if(uri != null && uri.startsWith("/WEB-INF")) {
            req.getServletContext().getNamedDispatcher("jsp").forward(req, res);
            return;
        }
        UrlMethod method = new UrlMethod();
        method.setUrl(uri);
        method.setMethod(req.getMethod());
        Mapping mapping = urlMappings.get(method);
        if(mapping == null) {
            UrlNotFoundException ex = new UrlNotFoundException(urlMappings, method);
            throw new ServletException(ex);
        }
        
        try {
            Object result = Utils.invokeMapping(mapping,this.getServletContext());
            if(result instanceof ModelAndView) {
                ModelAndView castedResult = (ModelAndView) result;
                String viewName = prefix + castedResult.getViewName() + suffix;
                // out.println(viewName);
                RequestDispatcher dis = req.getRequestDispatcher(viewName);
                for(Map.Entry<String, Object> entry : castedResult.getAttributes().entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                // out.println(dis);
                dis.forward(req, res);
                return;
            }
            try(PrintWriter out = res.getWriter();) {
                out.println(result);
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}
