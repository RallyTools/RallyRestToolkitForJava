package com.rallydev.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rallydev.rest.client.ApiKeyClient;
import com.rallydev.rest.client.BasicAuthClient;
import com.rallydev.rest.request.*;
import com.rallydev.rest.response.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

@Test
public class RallyRestApiTest {
    private RallyRestApi api;
    private URI server;

    @BeforeMethod
    protected void setUp() throws Exception {
        server = new URI("https://someServer.rallydev.com");
        api = new RallyRestApi(server, "_1adfkj234fjlk");
        api.client = spy(api.client);

    }

    public void shouldInitializeBasicAuthClient() {
        RallyRestApi basicAuthApi = new RallyRestApi(server, "username", "password");
        Assert.assertTrue(basicAuthApi.getClient() instanceof BasicAuthClient);
    }

    public void shouldInitializeApiKeyClient() {
        RallyRestApi apiKeyApi = new RallyRestApi(server, "apiKey");
        Assert.assertTrue(apiKeyApi.getClient() instanceof ApiKeyClient);
    }

    public void shouldSetProxy() throws Exception {
        URI proxy = new URI("http://my.proxy.com:8000");
        api.setProxy(proxy);
        verify(api.client).setProxy(proxy);
    }

    public void shouldSetProxyWithCredentials() throws Exception {
        URI proxy = new URI("http://my.proxy.com:8000");
        api.setProxy(proxy, "username", "password");
        verify(api.client).setProxy(proxy, "username", "password");
    }

    public void shouldSetVendor() throws Exception {
        api.setApplicationVendor("fooVendor");
        verify(api.client).setApplicationVendor("fooVendor");
    }

    public void shouldSetVersion() throws Exception {
        api.setApplicationVersion("fooVersion");
        verify(api.client).setApplicationVersion("fooVersion");
    }

    public void shouldSetName() throws Exception {
        api.setApplicationName("fooName");
        verify(api.client).setApplicationName("fooName");
    }

    public void shouldGetWsapiVersion() {
        Assert.assertEquals(api.getWsapiVersion(), api.client.getWsapiVersion());
    }

    public void shouldSetWsapiVersion() {
        api.setWsapiVersion("1.43");
        Assert.assertEquals(api.getWsapiVersion(), api.client.getWsapiVersion());
        Assert.assertEquals(api.getWsapiVersion(), "1.43");
    }

    public void shouldCreate() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject createResult = new JsonObject();
        response.add("CreateResult", createResult);
        createResult.add("Errors", new JsonArray());
        createResult.add("Warnings", new JsonArray());
        JsonObject object = new JsonObject();
        object.addProperty("_ref", "/defect/1234");
        createResult.add("Object", object);

        JsonObject newDefect = new JsonObject();
        newDefect.addProperty("Name", "Foo");
        CreateRequest request = new CreateRequest("defect", newDefect);

        doReturn(new Gson().toJson(response)).when(api.client).doPost(request.toUrl(), request.getBody());
        CreateResponse createResponse = api.create(request);

        verify(api.client).doPost(request.toUrl(), request.getBody());
        Assert.assertTrue(createResponse.wasSuccessful());
        JsonObject createdObj = createResponse.getObject();
        assertEquals(createdObj.get("_ref").getAsString(), "/defect/1234");
    }

    public void shouldUpdate() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject updateResult = new JsonObject();
        response.add("OperationResult", updateResult);
        updateResult.add("Errors", new JsonArray());
        updateResult.add("Warnings", new JsonArray());
        JsonObject object = new JsonObject();
        object.addProperty("_ref", "/defect/1234");
        updateResult.add("Object", object);

        JsonObject updatedDefect = new JsonObject();
        updatedDefect.addProperty("Name", "Foo");
        UpdateRequest request = new UpdateRequest("/defect/1234", updatedDefect);
        doReturn(new Gson().toJson(response)).when(api.client).doPost(request.toUrl(), request.getBody());
        UpdateResponse updateResponse = api.update(request);

        verify(api.client).doPost(request.toUrl(), request.getBody());
        Assert.assertTrue(updateResponse.wasSuccessful());
        JsonObject obj = updateResponse.getObject();
        assertEquals(obj.get("_ref").getAsString(), "/defect/1234");
    }

    public void shouldUpdateCollection() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        response.add("OperationResult", result);
        result.add("Errors", new JsonArray());
        result.add("Warnings", new JsonArray());
        JsonArray results = new JsonArray();
        JsonObject tag = new JsonObject();
        tag.addProperty("_ref", "/tag/23456");
        results.add(tag);
        result.add("Results", results);

        JsonArray updatedTags = new JsonArray();

        updatedTags.add(tag);
        CollectionUpdateRequest request = new CollectionUpdateRequest("/defect/1234/tags", updatedTags, true);
        doReturn(new Gson().toJson(response)).when(api.client).doPost(request.toUrl(), request.getBody());
        CollectionUpdateResponse updateResponse = api.updateCollection(request);

        verify(api.client).doPost(request.toUrl(), request.getBody());
        Assert.assertTrue(updateResponse.wasSuccessful());
        JsonArray updateResults = updateResponse.getResults();
        assertEquals(updateResults.get(0).getAsJsonObject().get("_ref").getAsString(), "/tag/23456");
    }

    public void shouldDelete() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject deleteResult = new JsonObject();
        response.add("OperationResult", deleteResult);
        deleteResult.add("Errors", new JsonArray());
        deleteResult.add("Warnings", new JsonArray());

        DeleteRequest request = new DeleteRequest("/defect/1234");
        doReturn(new Gson().toJson(response)).when(api.client).doDelete(request.toUrl());
        DeleteResponse deleteResponse = api.delete(request);

        verify(api.client).doDelete(request.toUrl());
        Assert.assertTrue(deleteResponse.wasSuccessful());
    }

    public void shouldGet() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject defect = new JsonObject();
        response.add("Defect", defect);
        defect.add("Errors", new JsonArray());
        defect.add("Warnings", new JsonArray());
        defect.addProperty("_ref", "/defect/1234");

        GetRequest request = new GetRequest("/defect/1234");
        doReturn(new Gson().toJson(response)).when(api.client).doGet(request.toUrl());
        GetResponse getResponse = api.get(request);

        verify(api.client).doGet(request.toUrl());
        Assert.assertTrue(getResponse.wasSuccessful());
        JsonObject obj = getResponse.getObject();
        assertEquals(obj.get("_ref").getAsString(), "/defect/1234");
    }

    public void shouldQueryOnePage() throws Exception {
        JsonObject response = buildQueryResponse(5);
        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        doReturn(new Gson().toJson(response)).when(api.client).doGet(request.toUrl());
        QueryResponse queryResponse = api.query(request);

        verify(api.client, times(1)).doGet(anyString());
        verify(api.client).doGet(request.toUrl());
        Assert.assertTrue(queryResponse.wasSuccessful());
        assertEquals(queryResponse.getTotalResultCount(), 5);
    }

    public void shouldQueryAllPages() throws Exception {
        JsonObject response = buildQueryResponse(5);
        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setLimit(Integer.MAX_VALUE);
        doReturn(new Gson().toJson(response)).when(api.client).doGet(anyString());
        api.query(request);

        String requestUrl = request.toUrl();
        verify(api.client, times(5)).doGet(anyString()); //make sure 5 gets
        verify(api.client).doGet(requestUrl);
        verify(api.client).doGet(requestUrl.replace("start=1", "start=2"));
        verify(api.client).doGet(requestUrl.replace("start=1", "start=3"));
        verify(api.client).doGet(requestUrl.replace("start=1", "start=4"));
        verify(api.client).doGet(requestUrl.replace("start=1", "start=5"));
    }

    public void shouldQuerySomePages() throws Exception {
        JsonObject response = buildQueryResponse(5);

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(2);
        request.setLimit(4);
        doReturn(new Gson().toJson(response)).when(api.client).doGet(anyString());
        api.query(request);

        String requestUrl = request.toUrl();

        verify(api.client, times(2)).doGet(anyString()); //make sure 2 gets
        verify(api.client).doGet(requestUrl);
        verify(api.client).doGet(requestUrl.replace("start=1", "start=3"));
    }

    public void shouldQueryNoPages() throws Exception {
        JsonObject response = buildQueryResponse(0);

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setLimit(4);
        doReturn(new Gson().toJson(response)).when(api.client).doGet(anyString());
        api.query(request);

        verify(api.client, times(1)).doGet(anyString()); //make sure 1 get
        verify(api.client).doGet(request.toUrl());
    }

    public void shouldQuerySomePagesWithNonStandardStart() throws Exception {
        JsonObject response = buildQueryResponse(10);

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setStart(5);
        request.setLimit(4);
        doReturn(new Gson().toJson(response)).when(api.client).doGet(anyString());
        api.query(request);

        String requestUrl = request.toUrl();

        verify(api.client, times(4)).doGet(anyString()); //make sure 4 gets
        verify(api.client).doGet(requestUrl);
        verify(api.client).doGet(requestUrl.replace("start=5", "start=6"));
        verify(api.client).doGet(requestUrl.replace("start=5", "start=7"));
        verify(api.client).doGet(requestUrl.replace("start=5", "start=8"));
    }

    public void shouldQueryAllPagesWithNonStandardStart() throws Exception {
        JsonObject response = buildQueryResponse(10);

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setStart(5);
        request.setLimit(Integer.MAX_VALUE);
        doReturn(new Gson().toJson(response)).when(api.client).doGet(anyString());
        api.query(request);

        String requestUrl = request.toUrl();

        verify(api.client, times(6)).doGet(anyString()); //make sure 6 gets
        verify(api.client).doGet(requestUrl);
        verify(api.client).doGet(requestUrl.replace("start=5", "start=6"));
        verify(api.client).doGet(requestUrl.replace("start=5", "start=7"));
        verify(api.client).doGet(requestUrl.replace("start=5", "start=8"));
        verify(api.client).doGet(requestUrl.replace("start=5", "start=9"));
        verify(api.client).doGet(requestUrl.replace("start=5", "start=10"));
    }

    public void shouldClose() throws Exception {
        api.close();
        verify(api.client).close();
    }

    private JsonObject buildQueryResponse(int totalResultCount) {
        JsonObject response = new JsonObject();
        JsonObject queryResult = new JsonObject();
        response.add("QueryResult", queryResult);
        queryResult.add("Errors", new JsonArray());
        queryResult.add("Warnings", new JsonArray());
        queryResult.add("Results", new JsonArray());
        queryResult.addProperty("TotalResultCount", totalResultCount);
        return response;
    }

}
