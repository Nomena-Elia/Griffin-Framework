package griffin.mvc.helper;

import java.util.Enumeration;
import java.util.Set;

import griffin.mvc.web.servlet.GriffinSession;
import jakarta.servlet.http.HttpSession;

public class GriffinSessionHelper {
    public static void fillWithSession(GriffinSession session, HttpSession httpSession) {
        session.setCreationTime(httpSession.getCreationTime());
        session.setLastAccessedTime(httpSession.getLastAccessedTime());
        session.setMaxInactiveInterval(httpSession.getMaxInactiveInterval());
        Enumeration<String> attributesName = httpSession.getAttributeNames();
        while(attributesName.hasMoreElements()) {
            String attr = attributesName.nextElement();
            session.set(attr, httpSession.getAttribute(attr));
        }
    }

    public static void overrideHttpSession(GriffinSession session, HttpSession httpSession) {
        Set<String> keys = session.getAttributesName();
        for(String key : keys) {
            httpSession.setAttribute(key, session.get(key));
        }
    }
}
