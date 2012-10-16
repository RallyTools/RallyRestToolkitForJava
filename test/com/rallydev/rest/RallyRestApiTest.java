package com.rallydev.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.apache.http.client.methods.HttpRequestBase;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RallyRestApiTest {
    
    private RallyRestApi createApi() throws URISyntaxException {
        return new RallyRestApi(new URI("https://someServer.rallydev.com"), "username", "password");    
    }
    
    @Test
    public void shouldReturnCorrectWsapiUrl() throws URISyntaxException {
        RallyRestApi api = createApi();  
        Assert.assertEquals(api.buildWsapiUrl(), "https://someServer.rallydev.com/slm/webservice/" + RallyRestApi.DEFAULT_WSAPI_VERSION);
        api.setWsapiVersion("1.99");
        Assert.assertEquals(api.buildWsapiUrl(), "https://someServer.rallydev.com/slm/webservice/1.99");
    }

    @Test
    public void shouldCorrectlyCreate() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();

        JsonObject response = new JsonObject();
        JsonObject createResult = new JsonObject();       
        response.add("CreateResult", createResult);
        createResult.add("Errors", new JsonArray());
        createResult.add("Warnings", new JsonArray());
        JsonObject object = new JsonObject();
        object.addProperty("_ref", "/defect/1234.js");
        createResult.add("Object", object);
        
        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));
        
        JsonObject newDefect = new JsonObject();
        newDefect.addProperty("Name", "Foo");
        CreateRequest request = new CreateRequest("defect", newDefect);
        CreateResponse createResponse = apiSpy.create(request);
        
        verify(apiSpy).doPost(api.buildWsapiUrl() + request.toUrl(), request.getBody());
        Assert.assertTrue(createResponse.wasSuccessful());
        JsonObject createdObj = createResponse.getObject();
        Assert.assertEquals(createdObj.get("_ref").getAsString(), "/defect/1234.js");
    }

    @Test
    public void shouldCorrectlyGet() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();

        JsonObject response = new JsonObject();
        JsonObject defect = new JsonObject();
        response.add("Defect", defect);
        defect.add("Errors", new JsonArray());
        defect.add("Warnings", new JsonArray());
        defect.addProperty("_ref", "/defect/1234.js");

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        GetRequest request = new GetRequest("/defect/1234.js");
        GetResponse getResponse = apiSpy.get(request);

        verify(apiSpy).doGet(api.buildWsapiUrl() + request.toUrl());
        Assert.assertTrue(getResponse.wasSuccessful());
        JsonObject obj = getResponse.getObject();
        Assert.assertEquals(obj.get("_ref").getAsString(), "/defect/1234.js");
    }

    @Test
    public void shouldCorrectlyUpdate() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();

        JsonObject response = new JsonObject();
        JsonObject updateResult = new JsonObject();
        response.add("OperationResult", updateResult);
        updateResult.add("Errors", new JsonArray());
        updateResult.add("Warnings", new JsonArray());
        JsonObject object = new JsonObject();
        object.addProperty("_ref", "/defect/1234.js");
        updateResult.add("Object", object);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        JsonObject updatedDefect = new JsonObject();
        updatedDefect.addProperty("Name", "Foo");
        UpdateRequest request = new UpdateRequest("/defect/1234.js", updatedDefect);
        UpdateResponse updateResponse = apiSpy.update(request);

        verify(apiSpy).doPost(api.buildWsapiUrl() + request.toUrl(), request.getBody());
        Assert.assertTrue(updateResponse.wasSuccessful());
        JsonObject obj = updateResponse.getObject();
        Assert.assertEquals(obj.get("_ref").getAsString(), "/defect/1234.js");
    }

    @Test
    public void shouldCorrectlyDelete() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();

        JsonObject response = new JsonObject();
        JsonObject deleteResult = new JsonObject();
        response.add("OperationResult", deleteResult);
        deleteResult.add("Errors", new JsonArray());
        deleteResult.add("Warnings", new JsonArray());

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        DeleteRequest request = new DeleteRequest("/defect/1234.js");
        DeleteResponse deleteResponse = apiSpy.delete(request);

        verify(apiSpy).doDelete(api.buildWsapiUrl() + request.toUrl());
        Assert.assertTrue(deleteResponse.wasSuccessful());
    }

    @Test
    public void shouldCorrectlyQueryOnePage() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();
        JsonObject response = buildQueryResponse(5);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect"); 
        request.setPageSize(1);
        QueryResponse queryResponse = apiSpy.query(request);

        verify(apiSpy, times(1)).doGet(anyString()); //make sure only one get
        verify(apiSpy).doGet(api.buildWsapiUrl() + request.toUrl());
        Assert.assertTrue(queryResponse.wasSuccessful());
        Assert.assertEquals(queryResponse.getTotalResultCount(), 5);
    }

    @Test
    public void shouldCorrectlyQueryAllPages() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();
        JsonObject response = buildQueryResponse(5);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setLimit(Integer.MAX_VALUE);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(5)).doGet(anyString()); //make sure 5 gets
        verify(apiSpy).doGet(requestUrl);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=2"));
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=3"));
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=4"));
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=5"));
    }

    @Test
    public void shouldCorrectlyQuerySomePages() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();
        JsonObject response = buildQueryResponse(5);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(2);
        request.setLimit(4);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(2)).doGet(anyString()); //make sure 2 gets
        verify(apiSpy).doGet(requestUrl);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=3"));
    }

    @Test
    public void shouldCorrectlyQueryNoPages() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();
        JsonObject response = buildQueryResponse(0);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setLimit(4);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(1)).doGet(anyString()); //make sure 1 get
        verify(apiSpy).doGet(requestUrl);
    }

    @Test
    public void shouldCorrectlyQuerySomePagesWithNonStandardStart() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();
        JsonObject response = buildQueryResponse(10);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setStart(5);
        request.setLimit(4);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(4)).doGet(anyString()); //make sure 4 gets
        verify(apiSpy).doGet(requestUrl);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=6"));
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=7"));
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=8"));
    }

    @Test
    public void shouldCorrectlyQueryAllPagesWithNonStandardStart() throws URISyntaxException, IOException {
        RallyRestApi api = createApi();
        JsonObject response = buildQueryResponse(10);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setStart(5);
        request.setLimit(Integer.MAX_VALUE);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(6)).doGet(anyString()); //make sure 6 gets
        verify(apiSpy).doGet(requestUrl);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=6"));
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=7"));
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=8"));
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=9"));
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=10"));
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
