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
import com.rallydev.rest.util.InvalidURLException;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.mockito.ArgumentMatcher;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.rallydev.rest.RallyRestApi.OPERATION_RESULT_KEY;
import static com.rallydev.rest.RallyRestApi.QUERY_RESULT_KEY;
import static com.rallydev.rest.RallyRestApi.SECURITY_TOKEN_KEY;
import static com.rallydev.rest.RallyRestApi.SECURITY_TOKEN_PARAM_KEY;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class RallyRestApiTest {
    private RallyRestApi api;
    private String securityToken;
    private int attachTokenCount;
    private URI server;

    @BeforeMethod
    protected void setUp() throws Exception {
        attachTokenCount = 0;
        server = new URI("https://someServer.rallydev.com");
        api = new RallyRestApi(server, "username", "password") {
            @Override
            protected void attachSecurityInfo(HttpRequestBase request) throws IOException {
                attachTokenCount++;
            }
        };
        securityToken = UUID.randomUUID().toString();
        api.setSecurityToken(securityToken);
    }

    public void shouldHandleInvalidURLIfSecurityEndpointDoesNotExist() throws Exception {
        RallyRestApi apiSpy = spy(new RallyRestApi(server, "username", "password"));

        InvalidURLException exception = new InvalidURLException("invalid url");
        doThrow(exception).when(apiSpy).get(any(GetRequest.class), eq(true));
        HttpRequestBase request = new HttpGet(server);
        apiSpy.attachSecurityInfo(request);
        assertEquals(apiSpy.getSecurityToken(), RallyRestApi.SECURITY_ENDPOINT_DOES_NOT_EXIST);
        verify(apiSpy, times(1)).get(any(GetRequest.class), eq(true));

        apiSpy.attachSecurityInfo(request);
        //validate does not get called again once known sec endpoint does not exist
        verify(apiSpy, times(1)).get(any(GetRequest.class), eq(true));
    }

    public void shouldAttachTheSecurityTokenToRequestWithExistingTokenValue() throws Exception {
        api = new RallyRestApi(server, "username", "password");
        api.setSecurityToken(securityToken);

        HttpRequestBase request = new HttpGet(server);
        api.attachSecurityInfo(request);
        validateSecurityTokenParam(request);
    }

    public void shouldAttachTheSecurityTokenToRequestWithoutExistingTokenValue() throws Exception {
        RallyRestApi apiSpy = spy(new RallyRestApi(server, "username", "password"));

        JsonObject getResult = new JsonObject();
        getResult.add("Errors", new JsonArray());
        getResult.add("Warnings", new JsonArray());
        getResult.addProperty(SECURITY_TOKEN_KEY, securityToken);
        JsonObject response = new JsonObject();
        response.add(OPERATION_RESULT_KEY, getResult);

        GetResponse toBeReturned = new GetResponse(new Gson().toJson(response));
        doReturn(toBeReturned).when(apiSpy).get(any(GetRequest.class), eq(true));

        HttpRequestBase request = new HttpGet(server);
        apiSpy.attachSecurityInfo(request);
        verify(apiSpy, times(1)).get(any(GetRequest.class), eq(true));
        validateSecurityTokenParam(request);
    }

    private void validateSecurityTokenParam(HttpRequestBase request) {
        List<NameValuePair> params = new URIBuilder(request.getURI()).getQueryParams();
        boolean foundToken = false;
        for (NameValuePair nvp : params) {
            if (nvp.getName().equals(SECURITY_TOKEN_PARAM_KEY) && nvp.getValue().equals(securityToken)) {
                foundToken = true;
            }
        }
        assertTrue(foundToken);
    }

    public void shouldCorrectlyCreateInWsapi1() throws Exception {
        api.setWsapiVersion("1.43");
        assertCanCreate();
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyCreateInWsapi2() throws Exception {
        api.setWsapiVersion("v2.0");
        assertCanCreate();
        assertEquals(attachTokenCount, 1);
    }

    public void shouldCorrectlyUpdateInWsapi1() throws Exception {
        api.setWsapiVersion("1.43");
        assertCanUpdate();
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyUpdateInWsapi2() throws Exception {
        api.setWsapiVersion("v2.0");
        assertCanUpdate();
        assertEquals(attachTokenCount, 1);
    }

    public void shouldCorrectlyDeleteInWsapi1() throws Exception {
        api.setWsapiVersion("1.43");
        assertCanDelete();
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyDeleteInWsapi2() throws Exception {
        api.setWsapiVersion("v2.0");
        assertCanDelete();
        assertEquals(attachTokenCount, 1);
    }

    public void shouldCorrectlyGet() throws Exception {
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

        verify(apiSpy).doGet(api.buildWsapiUrl() + request.toUrl(), false);
        Assert.assertTrue(getResponse.wasSuccessful());
        JsonObject obj = getResponse.getObject();
        assertEquals(obj.get("_ref").getAsString(), "/defect/1234.js");
        assertEquals(attachTokenCount, 0);
    }

    public void shouldReturnCorrectWsapiUrl() throws Exception {
        assertEquals(api.buildWsapiUrl(), "https://someServer.rallydev.com/slm/webservice/" + RallyRestApi.DEFAULT_WSAPI_VERSION);
        api.setWsapiVersion("1.99");
        assertEquals(api.buildWsapiUrl(), "https://someServer.rallydev.com/slm/webservice/1.99");
    }

    public void shouldCorrectlyEncodePutInUtf8() throws Exception {
        JsonObject response = new JsonObject();

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        apiSpy.doPut("url", "body");
        verify(apiSpy).doRequest(argThat(new HttpRequestUtf8Matcher()));
    }

    public void shouldCorrectlyEncodePostInUtf8() throws Exception {
        JsonObject response = new JsonObject();

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        apiSpy.doPost("url", "body");
        verify(apiSpy).doRequest(argThat(new HttpRequestUtf8Matcher()));
    }

    public void shouldCorrectlyQueryOnePage() throws Exception {
        JsonObject response = buildQueryResponse(5);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        QueryResponse queryResponse = apiSpy.query(request);

        verify(apiSpy, times(1)).doGet(anyString(), eq(false)); //make sure only one get
        verify(apiSpy).doGet(api.buildWsapiUrl() + request.toUrl(), false);
        Assert.assertTrue(queryResponse.wasSuccessful());
        assertEquals(queryResponse.getTotalResultCount(), 5);
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyQueryAllPages() throws Exception {
        JsonObject response = buildQueryResponse(5);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setLimit(Integer.MAX_VALUE);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(5)).doGet(anyString(), eq(false)); //make sure 5 gets
        verify(apiSpy).doGet(requestUrl, false);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=2"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=3"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=4"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=5"), false);
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyQuerySomePages() throws Exception {
        JsonObject response = buildQueryResponse(5);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(2);
        request.setLimit(4);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(2)).doGet(anyString(), eq(false)); //make sure 2 gets
        verify(apiSpy).doGet(requestUrl, false);
        verify(apiSpy).doGet(requestUrl.replace("start=1", "start=3"), false);
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyQueryNoPages() throws Exception {
        JsonObject response = buildQueryResponse(0);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setLimit(4);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(1)).doGet(anyString(), eq(false)); //make sure 1 get
        verify(apiSpy).doGet(requestUrl, false);
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyQuerySomePagesWithNonStandardStart() throws Exception {
        JsonObject response = buildQueryResponse(10);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setStart(5);
        request.setLimit(4);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(4)).doGet(anyString(), eq(false)); //make sure 4 gets
        verify(apiSpy).doGet(requestUrl, false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=6"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=7"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=8"), false);
        assertEquals(attachTokenCount, 0);
    }

    public void shouldCorrectlyQueryAllPagesWithNonStandardStart() throws Exception {
        JsonObject response = buildQueryResponse(10);

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        QueryRequest request = new QueryRequest("Defect");
        request.setPageSize(1);
        request.setStart(5);
        request.setLimit(Integer.MAX_VALUE);
        apiSpy.query(request);

        String requestUrl = api.buildWsapiUrl() + request.toUrl();

        verify(apiSpy, times(6)).doGet(anyString(), eq(false)); //make sure 6 gets
        verify(apiSpy).doGet(requestUrl, false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=6"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=7"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=8"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=9"), false);
        verify(apiSpy).doGet(requestUrl.replace("start=5", "start=10"), false);
        assertEquals(attachTokenCount, 0);
    }

    private JsonObject buildQueryResponse(int totalResultCount) {
        JsonObject response = new JsonObject();
        JsonObject queryResult = new JsonObject();
        response.add(QUERY_RESULT_KEY, queryResult);
        queryResult.add("Errors", new JsonArray());
        queryResult.add("Warnings", new JsonArray());
        queryResult.add("Results", new JsonArray());
        queryResult.addProperty("TotalResultCount", totalResultCount);
        return response;
    }

    private void assertCanCreate() throws Exception {
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
        assertEquals(createdObj.get("_ref").getAsString(), "/defect/1234.js");
        //assertEquals(attachTokenCount, 1);
    }

    private void assertCanUpdate() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject updateResult = new JsonObject();
        response.add(OPERATION_RESULT_KEY, updateResult);
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
        assertEquals(obj.get("_ref").getAsString(), "/defect/1234.js");
    }

    private void assertCanDelete() throws Exception {
        JsonObject response = new JsonObject();
        JsonObject deleteResult = new JsonObject();
        response.add(OPERATION_RESULT_KEY, deleteResult);
        deleteResult.add("Errors", new JsonArray());
        deleteResult.add("Warnings", new JsonArray());

        RallyRestApi apiSpy = spy(api);
        doReturn(new Gson().toJson(response)).when(apiSpy).doRequest(any(HttpRequestBase.class));

        DeleteRequest request = new DeleteRequest("/defect/1234.js");
        DeleteResponse deleteResponse = apiSpy.delete(request);

        verify(apiSpy).doDelete(api.buildWsapiUrl() + request.toUrl());
        Assert.assertTrue(deleteResponse.wasSuccessful());
    }

    class HttpRequestUtf8Matcher extends ArgumentMatcher<HttpEntityEnclosingRequestBase> {
        public boolean matches(Object o) {
            if (o instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase h = (HttpEntityEnclosingRequestBase) o;
                Header contentType = h.getEntity().getContentType();
                return contentType.getValue().toLowerCase().contains("utf-8");
            }
            return false;
        }
    }
}
