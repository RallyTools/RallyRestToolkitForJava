package com.rallydev.rest.response;

import static com.rallydev.rest.RallyRestApi.OPERATION_RESULT_KEY;

/**
 * Represents a WSAPI response from deleting an object
 */
public class DeleteResponse extends Response {

    /**
     * Create a new delete response from the specified JSON encoded string.
     *
     * @param deleteResponse the JSON encoded string
     */
    public DeleteResponse(String deleteResponse) {
        super(deleteResponse);
    }

    /**
     * Get the name of the root JSON result
     *
     * @return the root element name
     */
    @Override
    protected String getRoot() {
        return OPERATION_RESULT_KEY;
    }
}
