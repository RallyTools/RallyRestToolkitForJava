package com.rallydev.rest.client;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.net.URI;

/**
 * A HttpClient which authenticates using an API Key.
 */
public class ApiKeyClient extends HttpClient {

    protected String apiKey;
    protected static final String API_KEY_HEADER = "zsessionid";

    /**
     * Construct a new client.
     * @param server the server to connect to
     * @param apiKey the key to be used for authentication
     */
    public ApiKeyClient(URI server, String apiKey) {
        super(server);
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
    @Override
    protected String doRequest(HttpRequestBase request) throws IOException {
        request.setHeader(API_KEY_HEADER, this.apiKey);
        return super.doRequest(request);
    }
}