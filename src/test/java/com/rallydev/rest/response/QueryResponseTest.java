package com.rallydev.rest.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryResponseTest {
    private QueryResponse createResponse(String[] errors, JsonArray results) {
        return new QueryResponse("{\"QueryResult\": { \"Errors\": " + new Gson().toJson(errors) + ", \"Warnings\": [], \"TotalResultCount\": "  +
                results.size() + ", \"Results\": " + new Gson().toJson(results) + ", \"PageSize\": 20, \"StartIndex\": 1}}");
    }

    @Test
    public void shouldReturnResults() {
        JsonArray results = new JsonArray();
        JsonObject r1 = new JsonObject();
        r1.addProperty("Foo", 7);
        results.add(r1);
        JsonObject r2 = new JsonObject();
        r2.addProperty("Foo", 1);
        results.add(r2);
        
        QueryResponse r = createResponse(new String[]{}, results);
        
        Assert.assertEquals(r.getErrors().length, 0, "Error length correct");
        Assert.assertEquals(r.getTotalResultCount(), 2, "Total result count correct");
        Assert.assertEquals(r.getResults().size(), 2, "Result length correct");
        Assert.assertEquals(r.getResults().get(0).getAsJsonObject().get("Foo").getAsInt(), 7, "First result correct");
        Assert.assertEquals(r.getResults().get(1).getAsJsonObject().get("Foo").getAsInt(), 1, "First result correct");
        Assert.assertTrue(r.wasSuccessful());
        Assert.assertEquals(r.getPageSize(), 20);
        Assert.assertEquals(r.getStart(), 1);
    }

    @Test
    public void shouldReturnNoErrors() {
        QueryResponse r = createResponse(new String[]{"Foo"}, new JsonArray());
        
        Assert.assertEquals(r.getErrors().length, 1, "Error length correct");
        Assert.assertEquals(r.getErrors()[0], "Foo");
        Assert.assertFalse(r.wasSuccessful());
        Assert.assertEquals(r.getTotalResultCount(), 0);
        Assert.assertEquals(r.getResults().size(), 0);
    }
}
