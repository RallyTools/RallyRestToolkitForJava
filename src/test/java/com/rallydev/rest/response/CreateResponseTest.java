package com.rallydev.rest.response;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateResponseTest {
    private CreateResponse createResponse(String[] errors, String result) {
        return new CreateResponse("{\"CreateResult\": { \"Errors\": " + new Gson().toJson(errors) + ", \"Warnings\": [], \"Object\": "  +
                result + "}}");
    }

    @Test
    public void shouldReturnCreatedObject() {
        CreateResponse createResponse = createResponse(new String[]{}, "{\"Foo\": 7}");
       
        Assert.assertEquals(createResponse.getErrors().length, 0, "Error length correct");
        Assert.assertEquals(createResponse.getWarnings().length, 0, "Warning length correct");
        Assert.assertNotNull(createResponse.getObject());
        Assert.assertEquals(createResponse.getObject().get("Foo").getAsInt(), 7);
        Assert.assertTrue(createResponse.wasSuccessful());
    }

    @Test
    public void shouldReturnErrors() {
        CreateResponse createResponse = createResponse(new String[]{"Foo"}, "{}");
        String[] errors = createResponse.getErrors();
        Assert.assertEquals(errors.length, 1, "Error length correct");
        Assert.assertFalse(createResponse.wasSuccessful());
        Assert.assertNull(createResponse.getObject());
    }
}
