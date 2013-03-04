package com.rallydev.rest.request;

import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import org.apache.http.client.utils.URLEncodedUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateRequestTest {

    @Test
    public void shouldCreateACorrectBody() {
        JsonObject body = new JsonObject();
        body.addProperty("Name", "My Story");
        CreateRequest req = new CreateRequest("HierarchicalRequirement", body);
        Assert.assertEquals(req.getBody(), "{\"HierarchicalRequirement\":{\"Name\":\"My Story\"}}");
    }

    @Test
    public void shouldConstructTheCorrectUrl() {
        JsonObject body = new JsonObject();
        CreateRequest req = new CreateRequest("Defect", body);
        req.setFetch(new Fetch("Name", "Description"));
        Assert.assertEquals(req.toUrl(), "/defect/create.js?fetch=Name%2CDescription");
    }

    @Test
    public void shouldConstructTheCorrectDefaultFetchUrl() {
        JsonObject body = new JsonObject();
        CreateRequest req = new CreateRequest("Defect", body);
        Assert.assertEquals(req.toUrl(), "/defect/create.js?fetch=true");
    }

    @Test
    public void shouldConstructTheCorrectUrlWithExtraParam() {
        JsonObject body = new JsonObject();
        CreateRequest req = new CreateRequest("Defect", body);
        req.addParam("foo", "Bar");
        Assert.assertEquals(req.toUrl(), "/defect/create.js?foo=Bar&fetch=true");
    }

    @Test
    public void shouldEncodeParamsUsingUtf8() {
        JsonObject body = new JsonObject();
        CreateRequest req = new CreateRequest("Defect", body);
        req.addParam("foo", "备");
        Assert.assertEquals(req.toUrl(), "/defect/create.js?" + 
                URLEncodedUtils.format(req.getParams(), "utf-8") + "&fetch=true");
    }
}
