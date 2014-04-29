package com.rallydev.rest.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.DeleteRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.DeleteResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.InvalidURLException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class BasicAuthClient extends BaseClient {

    protected static final String SECURITY_ENDPOINT_DOES_NOT_EXIST = "SECURITY_ENDPOINT_DOES_NOT_EXIST";
    protected static final String SECURITY_TOKEN_PARAM_KEY = "key";
    private static final String SECURITY_TOKEN_URL = "/security/authorize";
    protected static final String SECURITY_TOKEN_KEY = "SecurityToken";
    private String securityToken;
    private Credentials credentials;

    public BasicAuthClient(URI server, String wsapiVersion, String userName, String password) {
        super(server, wsapiVersion);
        credentials = setClientCredentials(server, userName, password);
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
     * <p/>
     * Response Structure:
     * {"OperationResult": {"SecurityToken": "UUID"}}
     *
     * @param request the request to be modified
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
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
            } catch (InvalidURLException e) {
                //swallow the exception in this case as url does not exist indicates running and old version of
                //ALM without the security endpoint
                securityToken = SECURITY_ENDPOINT_DOES_NOT_EXIST;
            }
        }
    }

    /**
     * Sets the value on the security token for security enabled requests
     *
     * @param securityToken value fo the security token
     */
    protected void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    /**
     * Returns the current value of the security token.
     *
     * @return string value of security token
     */
    protected String getSecurityToken() {
        return securityToken;
    }
}