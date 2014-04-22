package com.rallydev.rest.matchers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.mockito.ArgumentMatcher;

public class HttpRequestBodyMatcher extends ArgumentMatcher<HttpEntityEnclosingRequestBase> {
    private String url;
    private String body;

    public HttpRequestBodyMatcher(String url, String body) {
        this.url = url;
        this.body = body;
    }

    public boolean matches(Object o) {
        if (o instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase h = (HttpEntityEnclosingRequestBase) o;
            Header contentType = h.getEntity().getContentType();
            return contentType.getValue().toLowerCase().contains("utf-8") &&
                    h.getURI().toString().equals(url) &&
                    h.getEntity().toString().equals(body);
        }
        return false;
    }
}