package com.rallydev.rest.response;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteResponseTest {
    
    private DeleteResponse createResponse(String[] errors) {
        return new DeleteResponse("{\"OperationResult\": { \"Errors\": " + new Gson().toJson(errors) + "}}"); 
    }

    @Test
    public void shouldReturnErrors() {
        DeleteResponse r = createResponse(new String[]{"Foo", "Bar"});
        String[] errors = r.getErrors();
        Assert.assertEquals(errors.length, 2, "Error length correct");
        Assert.assertFalse(r.wasSuccessful(), "Successful correct");
        Assert.assertEquals(errors[0], "Foo", "First error correct");
        Assert.assertEquals(errors[1], "Bar", "Second error correct");
    }

    @Test
    public void shouldReturnNoErrors() {
        DeleteResponse r = createResponse(new String[]{});
        String[] errors = r.getErrors();
        Assert.assertEquals(errors.length, 0, "Error length correct");
        Assert.assertTrue(r.wasSuccessful(), "Successful correct");
    }

}
