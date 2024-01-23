package com.rallydev.rest.client;

import com.rallydev.rest.matchers.HttpRequestHeaderMatcher;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.impl.auth.BasicScheme;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BasicAuthClientTest {

    private String server = "https://rally1.rallydev.com";
    private String userName = "foo";
    private String password = "bar";
    private BasicAuthClient client;

    private static String SECURITY_TOKEN_RESPONSE ="{\"OperationResult\":{\"SecurityToken\":\"foo\",\"Errors\":[]}}";

    @BeforeMethod
    public void setUp() throws URISyntaxException {
        BasicAuthClient client = new BasicAuthClient(new URI(server), userName, password);
        this.client = spy(client);
    }

    @Test
    public void shouldIntialize() {
        Assert.assertEquals(client.getServer(), server);
        Assert.assertEquals(client.credentials.getPassword(), password);
        Assert.assertEquals(client.credentials.getUserPrincipal().getName(), userName);
    }

    @Test
    public void shouldNotIncludeCSRFTokenOnGet() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.doRequest(new HttpGet());
        verify(client, times(0)).attachSecurityInfo(any(HttpRequestBase.class));
    }

    @Test
    public void shouldNotIncludeCSRFTokenOnWsapiv1() throws Exception {
        client.setWsapiVersion("1.43");
        doReturn("").when(client).executeRequest(any(HttpRequestBase.class));
        client.doRequest(new HttpPost());
        verify(client, times(0)).attachSecurityInfo(any(HttpRequestBase.class));
    }

    @Test
    public void shouldRequestCSRFToken() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpPost.class));
        doReturn(SECURITY_TOKEN_RESPONSE).when(client).executeRequest(any(HttpGet.class));
        client.doRequest(new HttpPost(server));
        Header authHeader = BasicScheme.authenticate(client.credentials, "utf-8", false);
        verify(client).executeRequest(argThat(new HttpRequestHeaderMatcher(authHeader.getName(), authHeader.getValue())));
    }

    @Test
    public void shouldIncludeCSRFTokenOnPost() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpPost.class));
        doReturn(SECURITY_TOKEN_RESPONSE).when(client).executeRequest(any(HttpGet.class));
        HttpPost post = new HttpPost(server);
        client.doRequest(post);
        post.getURI().getQuery().contains(BasicAuthClient.SECURITY_TOKEN_KEY + "=foo");
        verify(client).executeRequest(post);
    }

    @Test
    public void shouldIncludeCSRFTokenOnPut() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpPut.class));
        doReturn(SECURITY_TOKEN_RESPONSE).when(client).executeRequest(any(HttpGet.class));
        HttpPut put = new HttpPut(server);
        client.doRequest(put);
        put.getURI().getQuery().contains(BasicAuthClient.SECURITY_TOKEN_KEY + "=foo");
        verify(client).executeRequest(put);
    }

    @Test
    public void shouldIncludeCSRFTokenOnDelete() throws Exception {
        doReturn("").when(client).executeRequest(any(HttpDelete.class));
        doReturn(SECURITY_TOKEN_RESPONSE).when(client).executeRequest(any(HttpGet.class));
        HttpDelete delete = new HttpDelete(server);
        client.doRequest(delete);
        delete.getURI().getQuery().contains(BasicAuthClient.SECURITY_TOKEN_KEY + "=foo");
        verify(client).executeRequest(delete);
    }
}