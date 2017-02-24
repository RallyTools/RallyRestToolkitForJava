package com.rallydev.rest.response;

import com.google.gson.JsonArray;

/**
 * Represents a WSAPI response from updating a collection.
 */
public class CollectionUpdateResponse extends Response {

    /**
     * Create a new collection update response from the specified JSON encoded string.
     * Note that this class is only usable with WSAPI versions 2.0 and above.
     *
     * @param updateResponse the JSON encoded string
     */
    public CollectionUpdateResponse(String updateResponse) {
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
     * Get the results of the collection update
     * @return the results
     */
    public JsonArray getResults() {
        return result.getAsJsonArray("Results");
    }
}
