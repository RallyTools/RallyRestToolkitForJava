package com.rallydev.rest.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * A HttpClient implementation providing connectivity to Rally.  This class does not
 * provide any authentication on its own but instead relies on a concrete subclass to do so.
 */
public class HttpClient extends DefaultHttpClient
    implements Closeable {

    protected URI server;
    protected String wsapiVersion = "v2.0";
    protected DecompressingHttpClient client;

    private enum Header {
        Library,
        Name,
        Vendor,
        Version
    }

    private Map<Header, String> headers = new HashMap<Header, String>() {
        {
            put(Header.Library, "Rally Rest API for Java v2.2.1");
            put(Header.Name, "Rally Rest API for Java");
            put(Header.Vendor, "Rally Software, Inc.");
            put(Header.Version, "2.2.1");
        }
    };

    protected HttpClient(URI server) {
        this.server = server;
        client = new DecompressingHttpClient(this);
    }

    /**
     * Set the unauthenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     */
    public void setProxy(URI proxy) {
        this.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme()));
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
     * Get the current server being targeted.
     *
     * @return the current server.
     */
    public String getServer() {
        return server.toString();
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

        return this.executeRequest(request);
    }

    /**
     * Execute a request against the WSAPI
     *
     * @param request the request to be executed
     * @return the JSON encoded string response
     * @throws IOException if a non-200 response code is returned or if some other
     *                     problem occurs while executing the request
     */
    protected String executeRequest(HttpRequestBase request) throws IOException {
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(entity, "utf-8");
        } else {
            EntityUtils.consumeQuietly(entity);
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
        client.getConnectionManager().shutdown();
    }

    protected Credentials setClientCredentials(URI server, String userName, String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        this.getCredentialsProvider().setCredentials(new AuthScope(server.getHost(), server.getPort()), credentials);
        return credentials;
    }

    /**
     * Get the WSAPI base url based on the current server and WSAPI version
     *
     * @return the fully qualified WSAPI base url, e.g. https://rally1.rallydev.com/slm/webservice/1.33
     */
    public String getWsapiUrl() {
        return getServer() + "/slm/webservice/" + getWsapiVersion();
    }
}
