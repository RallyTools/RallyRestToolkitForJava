package com.rallydev.rest.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.rallydev.rest.matchers.HttpRequestHeaderMatcher;

public class ApiKeyClientTest {

    private String server = "https://rally1.rallydev.com";
    private String apiKey = "foo";
    private ApiKeyClient client;

    @BeforeMethod
    public void setUp() throws URISyntaxException {
        ApiKeyClient client = new ApiKeyClient(new URI(server), apiKey);
        this.client = spy(client);
    }

    @Test
    public void shouldIntialize() {
        Assert.assertEquals(client.getServer(), server);
    }

    @SuppressWarnings("resource")
    @Test
    public void shouldIntializePreConfiguredClient() throws URISyntaxException {
        HttpClient mockClient = mock(HttpClient.class);
        ApiKeyClient client = new ApiKeyClient(new URI(server), apiKey, mockClient);
        Assert.assertEquals(client.getServer(), server);
    }

    @Test
    public void shouldIncludeApiKeyOnRequest() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.doRequest(new HttpGet());
        verify(client).doRequest(argThat(new HttpRequestHeaderMatcher("zsessionid", apiKey)));
    }

    @Test
    public void shouldReturnPreConfiguredClient() throws Exception {
        DefaultHttpClient mockClient = mock(DefaultHttpClient.class);
        ApiKeyClient spiedClient = spy(new ApiKeyClient(new URI(server), apiKey, mockClient));
        Assert.assertEquals(spiedClient.client, mockClient);
    }
}
