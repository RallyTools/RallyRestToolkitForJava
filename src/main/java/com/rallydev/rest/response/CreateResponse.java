package com.rallydev.rest.response;

import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;

/**
 * Represents a WSAPI response from creating an object
 */
public class CreateResponse extends Response {

    /**
     * Create a new create response from the specified JSON encoded string.
     *
     * @param createResponse the JSON encoded string
     */
    public CreateResponse(String createResponse) {
        super(createResponse);
    }

    /**
     * Get the name of the root JSON result
     *
     * @return the root element name
     */
    @Override
    protected String getRoot() {
        return "CreateResult";
    }

    /**
     * Get the created object.
     * <p>Returns null if the operation was not successful</p>
     * @return the created object
     */
    public JsonObject getObject() {
        return wasSuccessful() ? result.getAsJsonObject("Object") : null;
    }
}
