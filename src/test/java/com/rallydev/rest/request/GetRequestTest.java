package com.rallydev.rest.request;

import com.rallydev.rest.util.Fetch;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetRequestTest {
    
    @Test
    public void shouldReturnCorrectUrlWithAbsoluteRef() {
        GetRequest req = new GetRequest("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlWithRelativeRef() {
        GetRequest req = new GetRequest("/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlWithFetchParams() {
        GetRequest req = new GetRequest("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js");
        req.setFetch(new Fetch("Name", "Description"));
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=Name%2CDescription");
    }

    @Test
    public void shouldReturnCorrectUrlForUser() {
        Assert.assertEquals(new GetRequest("User").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("user").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("/user").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("/user.js").toUrl(), "/user.js?fetch=true");
        Assert.assertEquals(new GetRequest("/user/12345.js").toUrl(), "/user/12345.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlForSubscription() {
        Assert.assertEquals(new GetRequest("Subscription").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("subscription").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("/subscription").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("/subscription.js").toUrl(), "/subscription.js?fetch=true");
        Assert.assertEquals(new GetRequest("/subscription/12345.js").toUrl(), "/subscription/12345.js?fetch=true");
    }
}
