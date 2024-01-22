package com.rallydev.rest.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.rallydev.rest.response.GetResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A HttpClient which authenticates using basic authentication (username/password).
 */
public class BasicAuthClient extends HttpClient {

    protected static final String SECURITY_ENDPOINT_DOES_NOT_EXIST = "SECURITY_ENDPOINT_DOES_NOT_EXIST";
    protected static final String SECURITY_TOKEN_PARAM_KEY = "key";
    private static final String SECURITY_TOKEN_URL = "/security/authorize";
    protected static final String SECURITY_TOKEN_KEY = "SecurityToken";
    protected String securityToken;
    protected Credentials credentials;

    /**
     * Construct a new client.
     * @param server the server to connect to
     * @param userName the username to be used for authentication
     * @param password the password to be used for authentication
     */
    public BasicAuthClient(URI server, String userName, String password) {
        super(server, userName, password);
        resetCredentials(userName, password);
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
        if(!request.getMethod().equals(HttpGet.METHOD_NAME) &&
                !this.getWsapiVersion().matches("^1[.]\\d+")) {
            try {
                attachSecurityInfo(request);
            } catch (URISyntaxException e) {
                throw new IOException("Unable to build URI with security token", e);
            }
        }
        return super.doRequest(request);
    }

    /**
     * Attach the security token parameter to the request.
     *
     * Response Structure:
     * {"OperationResult": {"SecurityToken": "UUID"}}
     *
     * @param request the request to be modified
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     * @throws URISyntaxException if there is a problem with the url in the request
     */
    protected void attachSecurityInfo(HttpRequestBase request) throws IOException, URISyntaxException {
        if (!SECURITY_ENDPOINT_DOES_NOT_EXIST.equals(securityToken)) {
            try {
                if (securityToken == null) {
                    HttpGet httpGet = new HttpGet(getWsapiUrl() + SECURITY_TOKEN_URL);
                    httpGet.addHeader(BasicScheme.authenticate(credentials, "utf-8", false));
                    GetResponse getResponse = new GetResponse(doRequest(httpGet));
                    JsonObject operationResult = getResponse.getObject();
                    JsonPrimitive securityTokenPrimitive = operationResult.getAsJsonPrimitive(SECURITY_TOKEN_KEY);
                    securityToken = securityTokenPrimitive.getAsString();
                }
                request.setURI(new URIBuilder(request.getURI()).addParameter(SECURITY_TOKEN_PARAM_KEY, securityToken).build());
            } catch (IOException e) {
                //swallow the exception in this case as url does not exist indicates running and old version of
                //ALM without the security endpoint
                securityToken = SECURITY_ENDPOINT_DOES_NOT_EXIST;
            }
        }
    }

    /**
     * Set the authenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy    The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     */
    public void setProxy(URI proxy, String userName, String password) {
        super.setProxy(proxy, userName, password);
        resetCredentials(userName, password);
    }

    private void resetCredentials(String userName, String password) {
        this.credentials = new UsernamePasswordCredentials(userName, password);
        securityToken = null;
    }
}