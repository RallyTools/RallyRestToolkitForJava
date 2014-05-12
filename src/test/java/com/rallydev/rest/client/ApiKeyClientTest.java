package com.rallydev.rest.client;

import com.rallydev.rest.matchers.HttpRequestHeaderMatcher;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class ApiKeyClientTest {

    private String server = "https://rally1.rallydev.com";
    private String apiKey = "foo";
    private ApiKeyClient client;

    @BeforeClass
    public void setUp() throws URISyntaxException {
        ApiKeyClient client = new ApiKeyClient(new URI(server), apiKey);
        this.client = spy(client);
    }

    @Test
    public void shouldIntialize() {
        Assert.assertEquals(client.getServer(), server);
    }

    @Test
    public void shouldIncludeApiKeyOnRequest() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.doRequest(new HttpGet());
        verify(client).doRequest(argThat(new HttpRequestHeaderMatcher("zsessionid", apiKey)));
    }
}
