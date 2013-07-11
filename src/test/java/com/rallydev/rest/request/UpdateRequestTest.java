package com.rallydev.rest.request;

import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UpdateRequestTest {

    @Test
    public void shouldCreateACorrectBody() {
        JsonObject body = new JsonObject();
        body.addProperty("Name", "My Story");
        UpdateRequest req = new UpdateRequest("https://rally1.rallydev.com/slm/webservice/1.32/hierarchicalrequirement/1234.js", body);
        Assert.assertEquals(req.getBody(), "{\"hierarchicalrequirement\":{\"Name\":\"My Story\"}}");
    }

    @Test
    public void shouldConstructTheCorrectUrl() {
        JsonObject body = new JsonObject();
        UpdateRequest req= new UpdateRequest("/defect/1234.js", body);
        req.setFetch(new Fetch("Name", "Description"));
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=Name%2CDescription");
    }

    @Test
    public void shouldConstructTheCorrectDefaultFetchUrl() {
        JsonObject body = new JsonObject();
        UpdateRequest req= new UpdateRequest("/defect/1234.js", body);
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?fetch=true");
    }

    @Test
    public void shouldConstructTheCorrectUrlWithExtraParam() {
        JsonObject body = new JsonObject();
        UpdateRequest req= new UpdateRequest("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js", body);
        req.addParam("foo", "Bar");
        Assert.assertEquals(req.toUrl(), "/defect/1234.js?foo=Bar&fetch=true");
    }
}
