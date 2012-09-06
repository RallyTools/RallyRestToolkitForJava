package com.rallydev.rest.request;

import com.rallydev.rest.util.Fetch;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetRequestTest {
    
    @Test
    public void shouldReturnCorrectUrlWithAbsoluteRef() {
        GetRequest req = new GetRequest("https://rally1.rallydev.com/slm/webservice/x/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlWithRelativeRef() {
        GetRequest req = new GetRequest("/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldReturnCorrectUrlWithFetchParams() {
        GetRequest req = new GetRequest("https://rally1.rallydev.com/slm/webservice/x/defect/1234.js");
        req.setFetch(new Fetch("Name", "Description"));
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=Name%2CDescription");
    }
}
