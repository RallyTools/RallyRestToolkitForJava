package com.rallydev.rest;

import com.google.gson.JsonArray;
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
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
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
        Version
    }

    /**
     * The default version of the WSAPI to target.
     */
    public static final String DEFAULT_WSAPI_VERSION = "1.40";

    protected URI server;
    protected URI proxy;
    protected String wsapiVersion = DEFAULT_WSAPI_VERSION;

    protected DefaultHttpClient httpClient;
    protected Map<Header, String> headers = new HashMap<Header, String>() {
        {
            put(Header.Library, "Rally Rest API for Java v1.0.7");
            put(Header.Name, "Rally Rest API for Java");
            put(Header.Vendor, "Rally Software, Inc.");
            put(Header.Version, "1.0.7");
        }
    };

    /**
     * Creates a new instance for the specified server using the specified credentials.
     * 
     * @param server The server to connect to, e.g. {@code new URI("https://rally1.rallydev.com")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     */
    public RallyRestApi(URI server, String userName, String password) {
        this.server = server;

        httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(server.getHost(), server.getPort()),
                new UsernamePasswordCredentials(userName, password));
    }

    /**
     * Set the unauthenticated proxy server to use.  By default no proxy is configured.
     * @param proxy The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     */
    public void setProxy(URI proxy) {
        this.proxy = proxy;
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme()));
    }

    /**
     * Set the authenticated proxy server to use.  By default no proxy is configured.
     * 
     * @param proxy The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     */
    public void setProxy(URI proxy, String userName, String password) {
        setProxy(proxy);
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(this.proxy.getHost(), this.proxy.getPort()),
                new UsernamePasswordCredentials(userName, password));
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
        return new CreateResponse(doPost(buildWsapiUrl() + request.toUrl(), request.getBody()));
    }

    /**
     * Update the specified object.
     *
     * @param request the {@link UpdateRequest} specifying the object to be updated.
     * @return the resulting {@link UpdateResponse} 
     * @throws IOException if an error occurs during the update.
     */
    public UpdateResponse update(UpdateRequest request) throws IOException {
        return new UpdateResponse(doPost(buildWsapiUrl() + request.toUrl(), request.getBody()));
    }

    /**
     * Delete the specified object.
     *
     * @param request the {@link DeleteRequest} specifying the object to be deleted.
     * @return the resulting {@link DeleteResponse} 
     * @throws IOException if an error occurs during the deletion.
     */
    public DeleteResponse delete(DeleteRequest request) throws IOException {
        return new DeleteResponse(doDelete(buildWsapiUrl() + request.toUrl()));
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
        QueryResponse queryResponse = new QueryResponse(doGet(buildWsapiUrl() + request.toUrl()));
        if (queryResponse.wasSuccessful()) {
            int receivedRecords = request.getPageSize();
            while (receivedRecords < request.getLimit() &&
                    (receivedRecords + request.getStart() - 1) < queryResponse.getTotalResultCount()) {
                QueryRequest pageRequest = request.clone();
                pageRequest.setStart(receivedRecords + request.getStart());
                QueryResponse pageResponse = new QueryResponse(doGet(buildWsapiUrl() + pageRequest.toUrl()));
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
        return new GetResponse(doGet(buildWsapiUrl() + request.toUrl()));
    }

    /**
     * Release all resources associated with this instance.
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
     *                
     * @return the JSON encoded string response
     * 
     * @throws IOException if a non-200 response code is returned or if some other
     * problem occurs while executing the request
     */
    protected String doRequest(HttpRequestBase request) throws IOException {

        //Set the headers
        for (Map.Entry<Header, String> header : headers.entrySet()) {
            request.setHeader("X-RallyIntegration" + header.getKey().name(), header.getValue());
        }

        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "utf-8");
        } else {
            throw new IOException(response.getStatusLine().toString());
        }
    }

    /**
     * Perform a post against the WSAPI
     * 
     * @param url the request url
     * @param body the body of the post
     *             
     * @return the JSON encoded string response
     * 
     * @throws IOException if a non-200 response code is returned or if some other
     * problem occurs while executing the request
     */
    protected String doPost(String url, String body) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(body, "utf-8"));
        return doRequest(httpPost);
    }

    /**
     * Perform a put against the WSAPI
     *
     * @param url the request url
     * @param body the body of the put
     *
     * @return the JSON encoded string response
     *
     * @throws IOException if a non-200 response code is returned or if some other
     * problem occurs while executing the request
     */
    protected String doPut(String url, String body) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new StringEntity(body, "utf-8"));
        return doRequest(httpPut);
    }

    /**
     * Perform a delete against the WSAPI
     *
     * @param url the request url
     *
     * @return the JSON encoded string response
     *
     * @throws IOException if a non-200 response code is returned or if some other
     * problem occurs while executing the request
     */
    protected String doDelete(String url) throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        return doRequest(httpDelete);
    }

    /**
     * Perform a get against the WSAPI
     *
     * @param url the request url
     *
     * @return the JSON encoded string response
     *
     * @throws IOException if a non-200 response code is returned or if some other
     * problem occurs while executing the request
     */
    protected String doGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return doRequest(httpGet);
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