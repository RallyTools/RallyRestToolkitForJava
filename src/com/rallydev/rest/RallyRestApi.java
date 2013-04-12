package com.rallydev.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.DeleteRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.DeleteResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.InvalidURLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The main interface to the Rest API.</p>
 * <p>Provides CRUD and query operations.</p>
 */
public class RallyRestApi implements Closeable {

    private enum Header {
        Library,
        Name,
        Vendor,
        Version;

    }
    /**
     * The default version of the WSAPI to target.
     */
    public static final String DEFAULT_WSAPI_VERSION = "1.42";
    public static final String CREATE_RESULT_KEY = "CreateResult";
    public static final String OPERATION_RESULT_KEY = "OperationResult";
    public static final String QUERY_RESULT_KEY = "QueryResult";

    protected static final String SECURITY_ENDPOINT_DOES_NOT_EXIST = "SECURITY_ENDPOINT_DOES_NOT_EXIST";
    protected static final String SECURITY_TOKEN_PARAM_KEY = "key";
    private static final String SECURITY_TOKEN_URL = "/security/authorize";
    protected static final String SECURITY_TOKEN_KEY = "SecurityToken";

    protected URI server;
    protected URI proxy;

    protected String wsapiVersion = DEFAULT_WSAPI_VERSION;
    protected DefaultHttpClient httpClient;
    private UsernamePasswordCredentials usernamePasswordCredentials;
    private String securityToken;

    protected Map<Header, String> headers = new HashMap<Header, String>() {
        {
            put(Header.Library, "Rally Rest API for Java v1.0.8");
            put(Header.Name, "Rally Rest API for Java");
            put(Header.Vendor, "Rally Software, Inc.");
            put(Header.Version, "1.0.8");
        }
    };

    /**
     * Creates a new instance for the specified server using the specified credentials.
     *
     * @param server   The server to connect to, e.g. {@code new URI("https://rally1.rallydev.com")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     */
    public RallyRestApi(URI server, String userName, String password) {
        this.server = server;
        httpClient = new DefaultHttpClient();
        setClientCredentials(server, userName, password);
    }

    /**
     * Set the unauthenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     */
    public void setProxy(URI proxy) {
        this.proxy = proxy;
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme()));
    }

    /**
     * Set the authenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy    The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     */
    public void setProxy(URI proxy, String userName, String password) {
        setProxy(proxy);
        setClientCredentials(proxy, userName, password);
    }

    private void setClientCredentials(URI server, String userName, String password) {
        usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(server.getHost(), server.getPort()),
                usernamePasswordCredentials);
    }

    /**
     * Set the value of the X-RallyIntegrationVendor header included on all requests.
     * This should be set to your company name.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationVendor(String value) {
        headers.put(Header.Vendor, value);
    }

    /**
     * Set the value of the X-RallyIntegrationVersion header included on all requests.
     * This should be set to the version of your application.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationVersion(String value) {
        headers.put(Header.Version, value);
    }

    /**
     * Set the value of the X-RallyIntegrationName header included on all requests.
     * This should be set to the name of your application.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationName(String value) {
        headers.put(Header.Name, value);
    }

    /**
     * Create the specified object.
     *
     * @param request the {@link CreateRequest} specifying the object to be created.
     * @return the resulting {@link CreateResponse}
     * @throws IOException if an error occurs during the creation.
     */
    public CreateResponse create(CreateRequest request) throws IOException {
        return create(request, true);
    }

    private CreateResponse create(CreateRequest request, boolean retryOnFail) throws IOException {
        CreateResponse createResponse = new CreateResponse(doPost(buildWsapiUrl() + request.toUrl(), request.getBody()));
        if (retryOnFail && createResponse.getErrors().length != 0) {
            setSecurityToken(null);
            return create(request, false);
        }
        return createResponse;
    }

    /**
     * Update the specified object.
     *
     * @param request the {@link UpdateRequest} specifying the object to be updated.
     * @return the resulting {@link UpdateResponse}
     * @throws IOException if an error occurs during the update.
     */
    public UpdateResponse update(UpdateRequest request) throws IOException {
        return update(request, true);
    }

    private UpdateResponse update(UpdateRequest request, boolean retryOnFail) throws IOException {
        UpdateResponse updateResponse = new UpdateResponse(doPost(buildWsapiUrl() + request.toUrl(), request.getBody()));
        if (retryOnFail && updateResponse.getErrors().length != 0) {
            setSecurityToken(null);
            return update(request, false);
        }
        return updateResponse;
    }

    /**
     * Delete the specified object.
     *
     * @param request the {@link DeleteRequest} specifying the object to be deleted.
     * @return the resulting {@link DeleteResponse}
     * @throws IOException if an error occurs during the deletion.
     */
    public DeleteResponse delete(DeleteRequest request) throws IOException {
        return delete(request, true);
    }

    private DeleteResponse delete(DeleteRequest request, boolean retryOnFail) throws IOException {
        DeleteResponse deleteResponse = new DeleteResponse(doDelete(buildWsapiUrl() + request.toUrl()));
        if (retryOnFail && deleteResponse.getErrors().length != 0) {
            setSecurityToken(null);
            return delete(request, false);
        }
        return deleteResponse;
    }

    /**
     * Query for objects matching the specified request.
     * By default one page of data will be returned.
     * Paging will automatically be performed if a limit is set on the request.
     *
     * @param request the {@link QueryRequest} specifying the object to be created.
     * @return the resulting {@link QueryResponse}
     * @throws IOException if an error occurs during the query.
     */
    public QueryResponse query(QueryRequest request) throws IOException {
        QueryResponse queryResponse = new QueryResponse(doGet(buildWsapiUrl() + request.toUrl(), false));
        if (queryResponse.wasSuccessful()) {
            int receivedRecords = request.getPageSize();
            while (receivedRecords < request.getLimit() &&
                    (receivedRecords + request.getStart() - 1) < queryResponse.getTotalResultCount()) {
                QueryRequest pageRequest = request.clone();
                pageRequest.setStart(receivedRecords + request.getStart());
                QueryResponse pageResponse = new QueryResponse(doGet(buildWsapiUrl() + pageRequest.toUrl(), false));
                if (pageResponse.wasSuccessful()) {
                    JsonArray results = queryResponse.getResults();
                    results.addAll(pageResponse.getResults());
                    receivedRecords += pageRequest.getPageSize();
                }
            }
        }

        return queryResponse;
    }

    /**
     * Get the specified object.
     *
     * @param request the {@link GetRequest} specifying the object to be retrieved.
     * @return the resulting {@link GetResponse}
     * @throws IOException if an error occurs during the retrieval.
     */
    public GetResponse get(GetRequest request) throws IOException {
        return get(request, false);
    }

    /**
     * Get the specified object. Forces basic auth as part of the request
     *
     * @param request the {@link GetRequest} specifying the object to be retrieved.
     * @return the resulting {@link GetResponse}
     * @throws IOException if an error occurs during the retrieval.
     */
    public GetResponse getWithForceReauth(GetRequest request) throws IOException {
        return get(request, true);
    }

    /**
     * Get the specified object. Forces basic auth as part of the request if specified
     *
     * @param request     the {@link GetRequest} specifying the object to be retrieved.
     * @param forceReauth boolean indicating if basic auth should be conducted as part of the request
     * @return the resulting {@link GetResponse}
     * @throws IOException if an error occurs during the retrieval.
     */
    private GetResponse get(GetRequest request, boolean forceReauth) throws IOException {
        return new GetResponse(doGet(buildWsapiUrl() + request.toUrl(), forceReauth));
    }

    /**
     * Release all resources associated with this instance.
     *
     * @throws IOException if an error occurs releasing resources
     */
    public void close() throws IOException {
        httpClient.getConnectionManager().shutdown();
    }

    /**
     * Get the current version of the WSAPI being targeted.
     * Defaults to {@link RallyRestApi#DEFAULT_WSAPI_VERSION}
     *
     * @return the current WSAPI version.
     */
    public String getWsapiVersion() {
        return wsapiVersion;
    }

    /**
     * Set the current version of the WSAPI being targeted.
     *
     * @param wsapiVersion the new version, e.g. {@code "1.30"}
     */
    public void setWsapiVersion(String wsapiVersion) {
        this.wsapiVersion = wsapiVersion;
    }

    /**
     * Execute a request against the WSAPI
     *
     * @param request the request to be executed
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doRequest(HttpRequestBase request) throws IOException {
        //Set the headers
        for (Map.Entry<Header, String> header : headers.entrySet()) {
            request.setHeader("X-RallyIntegration" + header.getKey().name(), header.getValue());
        }

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(entity, "utf-8");
        } else if (response.getStatusLine().getStatusCode() == 500) {
            ((BasicManagedEntity) entity).releaseConnection();
            throw new InvalidURLException(request.getURI().toString() + " is an Invalid URL");
        } else {
            throw new IOException(response.getStatusLine().toString());
        }
    }

    /**
     * Execute a request against the WSAPI with a basic auth header to force reauthorization
     *
     * @param request the request to be executed
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doForceReauthRequest(HttpRequestBase request) throws IOException {
        request.addHeader(BasicScheme.authenticate(usernamePasswordCredentials, "UTF-8", false));
        return doRequest(request);
    }

    /**
     * Execute a request against WSAPI that requires a security token due to data change/creation:
     * 1. Post
     * 2. Put
     * 3. Delete
     *
     * @param request the request to be executed
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    private String doSecurityEnableRequest(HttpRequestBase request) throws IOException {
        try {
            attachSecurityInfo(request);
        } catch (URISyntaxException e) {
            throw new IOException("Unable to build URI with security token", e);
        }
        return doRequest(request);
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
                    GetResponse getResponse = getWithForceReauth(new GetRequest(SECURITY_TOKEN_URL));
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

    /**
     * Perform a post against the WSAPI
     *
     * @param url  the request url
     * @param body the body of the post
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doPost(String url, String body) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(body, "utf-8"));
        return doSecurityEnableRequest(httpPost);
    }

    /**
     * Perform a put against the WSAPI
     *
     * @param url  the request url
     * @param body the body of the put
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doPut(String url, String body) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(body, "utf-8"));
        return doSecurityEnableRequest(httpPut);
    }

    /**
     * Perform a delete against the WSAPI
     *
     * @param url the request url
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doDelete(String url) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        return doSecurityEnableRequest(httpDelete);
    }

    /**
     * Perform a get against the WSAPI
     *
     * @param url         the request url
     * @param forceReauth
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String doGet(String url, boolean forceReauth) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return forceReauth ? doForceReauthRequest(httpGet) : doRequest(httpGet);
    }

    /**
     * Get the WSAPI base url based on the current server and WSAPI version
     *
     * @return the fully qualified WSAPI base url, e.g. https://rally1.rallydev.com/slm/webservice/1.33
     */
    protected String buildWsapiUrl() {
        return server.toString() + "/slm/webservice/" + wsapiVersion;
    }
}