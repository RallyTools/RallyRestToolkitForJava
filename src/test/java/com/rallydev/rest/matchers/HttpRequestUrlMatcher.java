package com.rallydev.rest.matchers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.mockito.ArgumentMatcher;

public class HttpRequestUrlMatcher extends ArgumentMatcher<HttpRequestBase> {
    private String url;
    private String value;

    public HttpRequestUrlMatcher(String url) {
        this.url = url;
    }

    public boolean matches(Object o) {
        if (o instanceof HttpRequestBase) {
            HttpRequestBase h = (HttpRequestBase) o;
            return h.getURI().toString().equals(url);
        }
        return false;
    }
}
