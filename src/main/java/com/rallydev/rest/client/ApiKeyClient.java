package com.rallydev.rest.client;

import com.rallydev.rest.util.InvalidURLException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class ApiKeyClient extends BaseClient {

    private String apiKey;
    protected static final String API_KEY_HEADER = "zsessionid";

    public ApiKeyClient(URI server, String wsapiVersion, String apiKey) {
        super(server, wsapiVersion);
        this.apiKey = apiKey;
    }

    /**
     * Execute a request against the WSAPI
     *
     * @param request the request to be executed
     * @return the JSON encoded string response
     * @throws java.io.IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doRequest(HttpRequestBase request) throws IOException {
        request.setHeader(API_KEY_HEADER, this.apiKey);
        return super.doRequest(request);
    }
}