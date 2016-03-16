package com.rallydev.rest.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectionUpdateRequestTest {

    @Test
    public void shouldCreateACorrectBody() {
        JsonArray items = new JsonArray();
        JsonObject tag = new JsonObject();
        tag.addProperty("_ref", "/tag/2345");
        items.add(tag);
        CollectionUpdateRequest req = new CollectionUpdateRequest("/hierarchicalrequirement/1234/tags.js", items, true);
        Assert.assertEquals(req.getBody(), "{\"CollectionItems\":[{\"_ref\":\"/tag/2345\"}]}");
    }

    @Test
    public void shouldConstructTheCorrectUrlForAdds() {
        JsonArray items = new JsonArray();
        CollectionUpdateRequest req = new CollectionUpdateRequest("/defect/1234/tags", items, true);
        req.setFetch(new Fetch("Name", "Description"));
        Assert.assertEquals(req.toUrl(), "/defect/1234/tags/add.js?fetch=Name%2CDescription");
    }

    @Test
    public void shouldConstructTheCorrectUrlForRemoves() {
        JsonObject tagsCollection = new JsonObject();
        tagsCollection.addProperty("_ref", "/defect/1234/tags");
        JsonArray items = new JsonArray();
        CollectionUpdateRequest req = new CollectionUpdateRequest(tagsCollection, items, false);
        Assert.assertEquals(req.toUrl(), "/defect/1234/tags/remove.js?fetch=true");
    }

    @Test
    public void shouldConstructTheCorrectUrlWithExtraParam() {
        JsonArray items = new JsonArray();
        CollectionUpdateRequest req = new CollectionUpdateRequest("/defect/1234/tags", items, true);
        req.addParam("foo", "Bar");
        Assert.assertEquals(req.toUrl(), "/defect/1234/tags/add.js?foo=Bar&fetch=true");
    }
}
