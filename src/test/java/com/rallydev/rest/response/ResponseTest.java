package com.rallydev.rest.response;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ResponseTest {
    
    private Response createResponse(String response) {
        return new Response("{\"Root\": " + response + "}") {
            @Override
            public String getRoot() {
                return "Root";
            }
        };
    }
    
    @Test
     public void shouldReturnErrors() {
        Response r = createResponse("{\"Errors\": [\"Foo\", \"Bar\"]}");
        String[] errors = r.getErrors();
        Assert.assertEquals(errors.length, 2, "Error length correct");
        Assert.assertFalse(r.wasSuccessful(), "Successful correct");
        Assert.assertEquals(errors[0], "Foo", "First error correct");
        Assert.assertEquals(errors[1], "Bar", "Second error correct");
    }

    @Test
    public void shouldReturnNoErrors() {
        Response r = createResponse("{\"Errors\": []}");
        String[] errors = r.getErrors();
        Assert.assertEquals(errors.length, 0, "Error length correct");
        Assert.assertTrue(r.wasSuccessful(), "Successful correct");
    }

    @Test
    public void shouldReturnWarnings() {
        Response r = createResponse("{\"Warnings\": [\"Foo\", \"Bar\"]}");
        String[] warnings = r.getWarnings();
        Assert.assertEquals(warnings.length, 2, "Error length correct");
        Assert.assertEquals(warnings[0], "Foo", "First error correct");
        Assert.assertEquals(warnings[1], "Bar", "Second error correct");
    }

    @Test
    public void shouldReturnNoWarnings() {
        Response r = createResponse("{\"Warnings\": []}");
        String[] warnings = r.getWarnings();
        Assert.assertEquals(warnings.length, 0, "Warning length correct");
    }
}
