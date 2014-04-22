package com.rallydev.rest.client;

import com.rallydev.rest.matchers.HttpRequestBodyMatcher;
import com.rallydev.rest.matchers.HttpRequestHeaderMatcher;
import com.rallydev.rest.matchers.HttpRequestUrlMatcher;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class HttpClientTest {

    private String server = "https://rally1.rallydev.com";
    private HttpClient client;

    @BeforeMethod
    public void setUp() throws URISyntaxException {
        HttpClient client = new HttpClient(new URI(server));
        this.client = spy(client);
    }

    @Test
    public void shouldIntialize() {
        Assert.assertEquals(client.getServer(), server);
    }

    @Test
    public void shouldSetProxy() throws Exception {
        URI proxy = new URI("http://my.proxy.com:8000");
        client.setProxy(proxy);
        Assert.assertEquals(client.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY),
                new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme()));
    }

    @Test
    public void shouldSetProxyWithCredentials() throws Exception {
        URI proxy = new URI("http://my.proxy.com:8000");
        client.setProxy(proxy, "username", "password");
        Assert.assertEquals(client.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY),
                new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme()));
        verify(client).setClientCredentials(proxy, "username", "password");
    }

    @Test
    public void shouldSetApplicationVendor() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.setApplicationVendor("FooVendor");
        client.doRequest(new HttpGet());
        verify(client).doRequest(argThat(new HttpRequestHeaderMatcher("X-RallyIntegrationVendor", "FooVendor")));
    }

    @Test
    public void shouldSetApplicationName() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.setApplicationName("FooName");
        client.doRequest(new HttpGet());
        verify(client).doRequest(argThat(new HttpRequestHeaderMatcher("X-RallyIntegrationName", "FooName")));
    }

    @Test
    public void shouldSetApplicationVersion() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.setApplicationVersion("FooVersion");
        client.doRequest(new HttpGet());
        verify(client).doRequest(argThat(new HttpRequestHeaderMatcher("X-RallyIntegrationVersion", "FooVersion")));
    }

    @Test
    public void shouldUseDefaultWsapiVersion() {
        Assert.assertEquals(client.getWsapiVersion(), "v2.0");
        Assert.assertEquals(client.getWsapiUrl(), server + "/slm/webservice/v2.0");
    }

    @Test
    public void shouldSetWsapiVersion() {
        client.setWsapiVersion("v3.0");
        Assert.assertEquals(client.getWsapiVersion(), "v3.0");
        Assert.assertEquals(client.getWsapiUrl(), server + "/slm/webservice/v3.0");
    }

    @Test
    public void shouldPost() throws Exception {
        String url = "/defect/12345";
        String body = "{}";
        doReturn("").when(client).doRequest(any(HttpPost.class));
        client.doPost(url, body);
        verify(client).doRequest(argThat(new HttpRequestBodyMatcher(client.getWsapiUrl() + url, body)));
    }

    @Test
    public void shouldPut() throws Exception {
        String url = "/defect/12345";
        String body = "{}";
        doReturn("").when(client).doRequest(any(HttpPut.class));
        client.doPut(url, body);
        verify(client).doRequest(argThat(new HttpRequestBodyMatcher(client.getWsapiUrl() + url, body)));
    }

    @Test
    public void shouldDelete() throws Exception {
        String url = "/defect/12345";
        doReturn("").when(client).doRequest(any(HttpDelete.class));
        client.doDelete(url);
        verify(client).doRequest(argThat(new HttpRequestUrlMatcher(client.getWsapiUrl() + url)));
    }

    @Test
    public void shouldGet() throws Exception {
        String url = "/defect/12345";
        doReturn("").when(client).doRequest(any(HttpGet.class));
        client.doGet(url);
        verify(client).doRequest(argThat(new HttpRequestUrlMatcher(client.getWsapiUrl() + url)));
    }

    @Test
    public void shouldGzip() throws Exception {
        String url = "/defect/1234";
        client.client = spy(client.client);
        doReturn(createMockResponse("{}")).when(client.client).execute(any(HttpGet.class));
        client.doGet(url);
        Assert.assertTrue(client.client instanceof DecompressingHttpClient);
        verify(client.client).execute(argThat(new HttpRequestUrlMatcher(client.getWsapiUrl() + url)));
    }

    @Test
    public void shouldReturnValidResponse() throws Exception {
        client.client = spy(client.client);
        doReturn(createMockResponse("{}")).when(client.client).execute(any(HttpGet.class));
        String response = client.doGet("/defect/1234");
        Assert.assertEquals("{}", response);
    }

    @Test(expectedExceptions = IOException.class)
    public void shouldExplodeWithInvalidResponse() throws Exception {
        client.client = spy(client.client);
        doReturn(createMockResponse("")).when(client.client).execute(any(HttpGet.class));
        client.doGet("/defect/1234");
    }

    private HttpResponse createMockResponse(String responseText) throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        StatusLine status = mock(StatusLine.class);
        when(response.getStatusLine()).thenReturn(status);
        when(status.getStatusCode()).thenReturn(responseText.length() == 0 ? 500 : 200);
        when(response.getEntity()).thenReturn(new StringEntity(responseText));
        return response;
    }
}