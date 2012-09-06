package com.rallydev.rest.response;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetResponseTest {
    
    private GetResponse createResponse(String[] errors) {
        return new GetResponse("{\"" + (errors.length > 0 ? "OperationResult": "Defect") +
                "\": { \"Errors\": " + new Gson().toJson(errors) + ", \"Foo\": \"Bar\"}}");
    }

    @Test
    public void shouldReturnErrors() {
        GetResponse r = createResponse(new String[]{"Foo", "Bar"});
        String[] errors = r.getErrors();
        Assert.assertEquals(errors.length, 2, "Error length correct");
        Assert.assertFalse(r.wasSuccessful(), "Successful correct");
        Assert.assertEquals(errors[0], "Foo", "First error correct");
        Assert.assertEquals(errors[1], "Bar", "Second error correct");
        Assert.assertNull(r.getObject());
    }

    @Test
    public void shouldReturnNoErrors() {
        GetResponse r = createResponse(new String[]{});
        String[] errors = r.getErrors();
        Assert.assertEquals(errors.length, 0, "Error length correct");
        Assert.assertTrue(r.wasSuccessful(), "Successful correct");
        Assert.assertNotNull(r.getObject());
        Assert.assertEquals(r.getObject().get("Foo").getAsString(), "Bar");
    }

}
