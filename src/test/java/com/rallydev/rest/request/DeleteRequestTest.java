package com.rallydev.rest.request;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteRequestTest {
    
    @Test
    public void shouldConstructCorrectUrlWithAbsoluteRef() {
        DeleteRequest req = new DeleteRequest("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js");
    }

    @Test
    public void shouldConstructCorrectUrlWithRelativeRef() {
        DeleteRequest req = new DeleteRequest("/defect/1234.js");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js");    
    }

    @Test
    public void shouldConstructCorrectUrlWithExtraParam() {
        DeleteRequest req = new DeleteRequest("/defect/1234.js");
        req.addParam("foo", "Bar");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?foo=Bar");
    }
}
