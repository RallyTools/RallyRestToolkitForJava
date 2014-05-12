//package com.rallydev.rest.client;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.rallydev.rest.RallyRestApi;
//import com.rallydev.rest.request.GetRequest;
//import com.rallydev.rest.response.GetResponse;
//import com.rallydev.rest.util.InvalidURLException;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.client.utils.URIBuilder;
//import java.util.List;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.eq;
//import static org.mockito.Mockito.*;
//import static org.testng.Assert.assertTrue;
//
///**
// * Created by kmorse on 5/6/14.
// */
//public class BasicAuthClientTest {
//
//    public void shouldHandleInvalidURLIfSecurityEndpointDoesNotExist() throws Exception {
//        RallyRestApi apiSpy = spy(new RallyRestApi(server, "username", "password"));
//
//        InvalidURLException exception = new InvalidURLException("invalid url");
//        doThrow(exception).when(apiSpy).get(any(GetRequest.class), eq(true));
//        HttpRequestBase request = new HttpGet(server);
//        apiSpy.attachSecurityInfo(request);
//        assertEquals(apiSpy.getSecurityToken(), RallyRestApi.SECURITY_ENDPOINT_DOES_NOT_EXIST);
//        verify(apiSpy, times(1)).get(any(GetRequest.class), eq(true));
//
//        apiSpy.attachSecurityInfo(request);
//        //validate does not get called again once known sec endpoint does not exist
//        verify(apiSpy, times(1)).get(any(GetRequest.class), eq(true));
//    }
//
//    public void shouldAttachTheSecurityTokenToRequestWithExistingTokenValue() throws Exception {
//        api = new RallyRestApi(server, "username", "password");
//        api.setSecurityToken(securityToken);
//
//        HttpRequestBase request = new HttpGet(server);
//        api.attachSecurityInfo(request);
//        validateSecurityTokenParam(request);
//    }
//
//    public void shouldAttachTheSecurityTokenToRequestWithoutExistingTokenValue() throws Exception {
//        RallyRestApi apiSpy = spy(new RallyRestApi(server, "username", "password"));
//
//        JsonObject getResult = new JsonObject();
//        getResult.add("Errors", new JsonArray());
//        getResult.add("Warnings", new JsonArray());
//        getResult.addProperty(SECURITY_TOKEN_KEY, securityToken);
//        JsonObject response = new JsonObject();
//        response.add("OperationResult", getResult);
//
//        GetResponse toBeReturned = new GetResponse(new Gson().toJson(response));
//        doReturn(toBeReturned).when(apiSpy).get(any(GetRequest.class), eq(true));
//
//        HttpRequestBase request = new HttpGet(server);
//        apiSpy.attachSecurityInfo(request);
//        verify(apiSpy, times(1)).get(any(GetRequest.class), eq(true));
//        validateSecurityTokenParam(request);
//    }
//
//    private void validateSecurityTokenParam(HttpRequestBase request) {
//        List<NameValuePair> params = new URIBuilder(request.getURI()).getQueryParams();
//        boolean foundToken = false;
//        for (NameValuePair nvp : params) {
//            if (nvp.getName().equals(SECURITY_TOKEN_PARAM_KEY) && nvp.getValue().equals(securityToken)) {
//                foundToken = true;
//            }
//        }
//        assertTrue(foundToken);
//    }
//
//    public void shouldCorrectlyCreateInWsapi1() throws Exception {
//        api.setWsapiVersion("1.43");
//        assertCanCreate();
//        assertEquals(attachTokenCount, 0);
//    }
//
//    public void shouldCorrectlyCreateInWsapi2() throws Exception {
//        api.setWsapiVersion("v2.0");
//        assertCanCreate();
//        assertEquals(attachTokenCount, 1);
//    }
//
//    public void shouldCorrectlyUpdateInWsapi1() throws Exception {
//        api.setWsapiVersion("1.43");
//        assertCanUpdate();
//        assertEquals(attachTokenCount, 0);
//    }
//
//    public void shouldCorrectlyUpdateInWsapi2() throws Exception {
//        api.setWsapiVersion("v2.0");
//        assertCanUpdate();
//        assertEquals(attachTokenCount, 1);
//    }
//
//    public void shouldCorrectlyDeleteInWsapi1() throws Exception {
//        api.setWsapiVersion("1.43");
//        assertCanDelete();
//        assertEquals(attachTokenCount, 0);
//    }
//
//    public void shouldCorrectlyDeleteInWsapi2() throws Exception {
//        api.setWsapiVersion("v2.0");
//        assertCanDelete();
//        assertEquals(attachTokenCount, 1);
//    }
//
//}
