package com.rallydev.rest.response;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UpdateResponseTest {
    private UpdateResponse UpdateResponse(String[] errors, String result) {
        return new UpdateResponse("{\"OperationResult\": { \"Errors\": " + new Gson().toJson(errors) + ", \"Warnings\": [], \"Object\": "  +
                result + "}}");
    }

    @Test
    public void shouldReturnUpdatedObject() {
        UpdateResponse UpdateResponse = UpdateResponse(new String[]{}, "{\"Foo\": 7}");

        Assert.assertEquals(UpdateResponse.getErrors().length, 0, "Error length correct");
        Assert.assertEquals(UpdateResponse.getWarnings().length, 0, "Warning length correct");
        Assert.assertNotNull(UpdateResponse.getObject());
        Assert.assertEquals(UpdateResponse.getObject().get("Foo").getAsInt(), 7);
        Assert.assertTrue(UpdateResponse.wasSuccessful());
    }

    @Test
    public void shouldReturnErrors() {
        UpdateResponse UpdateResponse = UpdateResponse(new String[]{"Foo"}, "{}");
        String[] errors = UpdateResponse.getErrors();
        Assert.assertEquals(errors.length, 1, "Error length correct");
        Assert.assertFalse(UpdateResponse.wasSuccessful());
        Assert.assertNull(UpdateResponse.getObject());
    }
}
