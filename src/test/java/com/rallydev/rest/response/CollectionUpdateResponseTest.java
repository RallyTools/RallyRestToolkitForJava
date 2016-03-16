package com.rallydev.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectionUpdateResponseTest {
    private CollectionUpdateResponse createResponse(String[] errors, JsonArray results) {
        return new CollectionUpdateResponse("{\"OperationResult\": { \"Errors\": " + new Gson().toJson(errors) + ", \"Warnings\": [], "  +
                "\"Results\": " + new Gson().toJson(results) + "}}");
    }

    @Test
    public void shouldReturnResults() {
        JsonArray results = new JsonArray();
        JsonObject r1 = new JsonObject();
        r1.addProperty("_ref", "/tag/12345");
        results.add(r1);
        JsonObject r2 = new JsonObject();
        r2.addProperty("_ref", "/tag/23456");
        results.add(r2);

        CollectionUpdateResponse r = createResponse(new String[]{}, results);
        
        Assert.assertEquals(r.getErrors().length, 0, "Error length correct");
        Assert.assertEquals(r.getResults().size(), 2, "Result length correct");
        Assert.assertEquals(r.getResults().get(0).getAsJsonObject().get("_ref").getAsString(), "/tag/12345", "First result correct");
        Assert.assertEquals(r.getResults().get(1).getAsJsonObject().get("_ref").getAsString(), "/tag/23456", "Second result correct");
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void shouldReturnNoErrors() {
        CollectionUpdateResponse r = createResponse(new String[]{"Foo"}, new JsonArray());
        
        Assert.assertEquals(r.getErrors().length, 1, "Error length correct");
        Assert.assertEquals(r.getErrors()[0], "Foo");
        Assert.assertFalse(r.wasSuccessful());
        Assert.assertEquals(r.getResults().size(), 0);
    }
}
