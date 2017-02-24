package com.rallydev.rest.matchers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.mockito.ArgumentMatcher;

public class HttpRequestHeaderMatcher extends ArgumentMatcher<HttpRequestBase> {
    private String name;
    private String value;

    public HttpRequestHeaderMatcher(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean matches(Object o) {
        if (o instanceof HttpRequestBase) {
            HttpRequestBase h = (HttpRequestBase) o;
            Header header = h.getFirstHeader(name);
            return header != null && header.getValue().equals(value);
        }
        return false;
    }
}
