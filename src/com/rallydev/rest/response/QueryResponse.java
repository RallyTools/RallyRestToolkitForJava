package com.rallydev.rest.response;

import com.google.gson.JsonArray;

import static com.rallydev.rest.RallyRestApi.QUERY_RESULT_KEY;

/**
 * Represents a WSAPI response from querying for objects.
 */
public class QueryResponse extends Response {

    /**
     * Create a new query response from the specified JSON encoded string.
     *
     * @param queryResponse the JSON encoded string
     */
    public QueryResponse(String queryResponse) {
        super(queryResponse);
    }

    /**
     * Get the name of the root JSON result
     *
     * @return the root element name
     */
    @Override
    protected String getRoot() {
        return QUERY_RESULT_KEY;
    }

    /**
     * Get the total number of objects that matched the query
     * 
     * @return the total number of objects
     */
    public int getTotalResultCount() {
        return result.get("TotalResultCount").getAsInt();
    }

    /**
     * Get the results of the query
     * <p>Depending on the limit of the original request this may include one or more pages.</p>
     * 
     * @return the results
     */
    public JsonArray getResults() {
        return result.getAsJsonArray("Results");
    }

    /**
     * Get the page size of the results
     * 
     * @return the page size
     */
    public int getPageSize() {
        return result.get("PageSize").getAsInt();
    }

    /**
     * Get the start index of the results
     * 
     * @return the start index
     */
    public int getStart() {
        return result.get("StartIndex").getAsInt();
    }
}
