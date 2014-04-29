package com.rallydev.rest.response;

import com.google.gson.JsonObject;

/**
 * Represents a WSAPI response from updating an object.
 */
public class UpdateResponse extends Response {

    /**
     * Create a new update response from the specified JSON encoded string.
     *
     * @param updateResponse the JSON encoded string
     */
    public UpdateResponse(String updateResponse) {
        super(updateResponse);
    }

    /**
     * Get the name of the root JSON result
     *
     * @return the root element name
     */
    @Override
    protected String getRoot() {
        return "OperationResult";
    }

    /**
     * Get the updated object.
     * <p>Returns null if the operation was not successful</p>
     * @return the updated object
     */
    public JsonObject getObject() {
        return wasSuccessful() ? result.getAsJsonObject("Object") : null;
    }
}
