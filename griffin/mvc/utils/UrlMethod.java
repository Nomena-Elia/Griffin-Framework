package griffin.mvc.utils;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private String method;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    @Override
    public boolean equals(Object other) {
        if(other == null || !other.getClass().equals(UrlMethod.class)) {
            return false;
        }
        UrlMethod casted = (UrlMethod) other;
        return getUrl().equals(casted.getUrl()) && getMethod().equals(casted.getMethod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getMethod());
    }

    @Override
    public String toString() {
        return method + ": " + url;
    }
}
