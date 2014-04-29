package com.rallydev.rest.client;

import com.rallydev.rest.util.InvalidURLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseClient implements Closeable {

    protected DefaultHttpClient httpClient;
    protected URI server;
    protected String wsapiVersion;

    private enum Header {
        Library,
        Name,
        Vendor,
        Version
    }

    private Map<Header, String> headers = new HashMap<Header, String>() {
        {
            put(Header.Library, "Rally Rest API for Java v2.1.0");
            put(Header.Name, "Rally Rest API for Java");
            put(Header.Vendor, "Rally Software, Inc.");
            put(Header.Version, "2.1.0");
        }
    };

    protected BaseClient(URI server, String wsapiVersion) {
        this.server = server;
        this.wsapiVersion = wsapiVersion;
        this.httpClient = new DefaultHttpClient();
    }

    /**
     * Set the unauthenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     */
    public void setProxy(URI proxy) {
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

    /**
     * Set the value of the X-RallyIntegrationVendor header included on all requests.
     * This should be set to your company name.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationVendor(String value) {
        headers.put(BaseClient.Header.Vendor, value);
    }

    /**
     * Set the value of the X-RallyIntegrationVersion header included on all requests.
     * This should be set to the version of your application.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationVersion(String value) {
        headers.put(BaseClient.Header.Version, value);
    }

    /**
     * Set the value of the X-RallyIntegrationName header included on all requests.
     * This should be set to the name of your application.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationName(String value) {
        headers.put(BaseClient.Header.Name, value);
    }

    /**
     * Get the current version of the WSAPI being targeted.
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
     * Get the underlying http client implementation.
     * This is exposed with the intent of providing the ability to supply additional configuration to the client.
     * It should not be used to directly make i/o calls.
     *
     * @return the raw http client
     */
    public DefaultHttpClient getRawHttpClient() {
        return httpClient;
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
     * Perform a post against the WSAPI
     *
     * @param url  the request url
     * @param body the body of the post
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    public String doPost(String url, String body) throws IOException {
        HttpPost httpPost = new HttpPost(getWsapiUrl() + url);
        httpPost.setEntity(new StringEntity(body, "utf-8"));
        return doRequest(httpPost);
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
    public String doPut(String url, String body) throws IOException {
        HttpPut httpPut = new HttpPut(getWsapiUrl() + url);
        httpPut.setEntity(new StringEntity(body, "utf-8"));
        return doRequest(httpPut);
    }

    /**
     * Perform a delete against the WSAPI
     *
     * @param url the request url
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    public String doDelete(String url) throws IOException {
        HttpDelete httpDelete = new HttpDelete(getWsapiUrl() + url);
        return doRequest(httpDelete);
    }

    /**
     * Perform a get against the WSAPI
     *
     * @param url the request url
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    public String doGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(getWsapiUrl() + url);
        return doRequest(httpGet);
    }

    /**
     * Release all resources associated with this instance.
     *
     * @throws IOException if an error occurs releasing resources
     */
    public void close() throws IOException {
        httpClient.getConnectionManager().shutdown();
    }

    protected Credentials setClientCredentials(URI server, String userName, String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        httpClient.getCredentialsProvider().setCredentials(new AuthScope(server.getHost(), server.getPort()), credentials);
        return credentials;
    }

    /**
     * Get the WSAPI base url based on the current server and WSAPI version
     *
     * @return the fully qualified WSAPI base url, e.g. https://rally1.rallydev.com/slm/webservice/1.33
     */
    protected String getWsapiUrl() {
        return server.toString() + "/slm/webservice/" + wsapiVersion;
    }
}