package com.rallydev.rest;

import com.google.gson.JsonArray;
import com.rallydev.rest.client.ApiKeyClient;
import com.rallydev.rest.client.BasicAuthClient;
import com.rallydev.rest.client.HttpClient;
import com.rallydev.rest.request.*;
import com.rallydev.rest.response.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

/**
 * <p>The main interface to the Rest API.</p>
 * <p>Provides CRUD and query operations.</p>
 */
public class RallyRestApi implements Closeable {

    protected HttpClient client;

    /**
     * Creates a new instance for the specified server using the specified credentials.
     *
     * @param server   The server to connect to, e.g. {@code new URI("https://rally1.rallydev.com")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     * @deprecated Use the api key constructor instead.
     */
    public RallyRestApi(URI server, String userName, String password) {
        this(new BasicAuthClient(server, userName, password));
    }

    /**
     * Creates a new instance for the specified server using the specified API Key.
     *
     * @param server The server to connect to, e.g. {@code new URI("https://rally1.rallydev.com")}
     * @param apiKey The API Key to be used for authentication.
     */
    public RallyRestApi(URI server, String apiKey) {
        this(new ApiKeyClient(server, apiKey));
    }

    protected RallyRestApi(HttpClient httpClient) {
        this.client = httpClient;
    }

    /**
     * Set the unauthenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     */
    public void setProxy(URI proxy) {
        client.setProxy(proxy);
    }

    /**
     * Set the authenticated proxy server to use.  By default no proxy is configured.
     *
     * @param proxy    The proxy server, e.g. {@code new URI("http://my.proxy.com:8000")}
     * @param userName The username to be used for authentication.
     * @param password The password to be used for authentication.
     */
    public void setProxy(URI proxy, String userName, String password) {
        client.setProxy(proxy, userName, password);
    }

    /**
     * Set the value of the X-RallyIntegrationVendor header included on all requests.
     * This should be set to your company name.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationVendor(String value) {
        client.setApplicationVendor(value);
    }

    /**
     * Set the value of the X-RallyIntegrationVersion header included on all requests.
     * This should be set to the version of your application.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationVersion(String value) {
        client.setApplicationVersion(value);
    }

    /**
     * Set the value of the X-RallyIntegrationName header included on all requests.
     * This should be set to the name of your application.
     *
     * @param value The vendor header to be included on all requests.
     */
    public void setApplicationName(String value) {
        client.setApplicationName(value);
    }

    /**
     * Get the current version of the WSAPI being targeted.
     * Defaults to v2.0
     *
     * @return the current WSAPI version.
     */
    public String getWsapiVersion() {
        return client.getWsapiVersion();
    }

    /**
     * Set the current version of the WSAPI being targeted.
     *
     * @param wsapiVersion the new version, e.g. {@code "1.30"}
     */
    public void setWsapiVersion(String wsapiVersion) {
        client.setWsapiVersion(wsapiVersion);
    }

    /**
     * Create the specified object.
     *
     * @param request the {@link CreateRequest} specifying the object to be created.
     * @return the resulting {@link CreateResponse}
     * @throws IOException if an error occurs during the creation.
     */
    public CreateResponse create(CreateRequest request) throws IOException {
        return new CreateResponse(client.doPost(request.toUrl(), request.getBody()));
    }

    /**
     * Update the specified object.
     *
     * @param request the {@link UpdateRequest} specifying the object to be updated.
     * @return the resulting {@link UpdateResponse}
     * @throws IOException if an error occurs during the update.
     */
    public UpdateResponse update(UpdateRequest request) throws IOException {
        return new UpdateResponse(client.doPost(request.toUrl(), request.getBody()));
    }

    /**
     * Update the specified collection.
     * Note that this method is only usable with WSAPI versions 2.0 and above.
     *
     * @param request the {@link CollectionUpdateRequest} specifying the collection to be updated.
     * @return the resulting {@link CollectionUpdateResponse}
     * @throws IOException if an error occurs during the update.
     */
    public CollectionUpdateResponse updateCollection(CollectionUpdateRequest request) throws IOException {
        return new CollectionUpdateResponse(client.doPost(request.toUrl(), request.getBody()));
    }

    /**
     * Delete the specified object.
     *
     * @param request the {@link DeleteRequest} specifying the object to be deleted.
     * @return the resulting {@link DeleteResponse}
     * @throws IOException if an error occurs during the deletion.
     */
    public DeleteResponse delete(DeleteRequest request) throws IOException {
        return new DeleteResponse(client.doDelete(request.toUrl()));
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
        QueryResponse queryResponse = new QueryResponse(client.doGet(request.toUrl()));
        if (queryResponse.wasSuccessful()) {
            int receivedRecords = request.getPageSize();
            while (receivedRecords < request.getLimit() &&
                    (receivedRecords + request.getStart() - 1) < queryResponse.getTotalResultCount()) {
                QueryRequest pageRequest = request.clone();
                pageRequest.setStart(receivedRecords + request.getStart());
                QueryResponse pageResponse = new QueryResponse(client.doGet(pageRequest.toUrl()));
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
        return new GetResponse(client.doGet(request.toUrl()));
    }

    /**
     * Release all resources associated with this instance.
     *
     * @throws IOException if an error occurs releasing resources
     */
    public void close() throws IOException {
        client.close();
    }

    /**
     * Get the underlying http client implementation.
     * This is exposed with the intent of providing the ability to supply additional configuration to the client.
     * It should not be used to directly make i/o calls.
     *
     * @return the raw http client
     */
    public HttpClient getClient() {
        return client;
    }
}