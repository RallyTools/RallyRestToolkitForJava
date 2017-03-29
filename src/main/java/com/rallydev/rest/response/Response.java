package com.rallydev.rest.response;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Represents a WSAPI response.
 */
public abstract class Response {

    private static final String ERRORS = "Errors";
    private static final String WARNINGS = "Warnings";

    protected JsonObject result;
    protected String raw;

    /**
     * Create a new response from the specified JSON encoded string.
     *
     * @param response the JSON encoded string
     */
    public Response(String response) {
        this.raw = response;
        this.result = ((JsonObject) new JsonParser().parse(response)).getAsJsonObject(getRoot());
    }

    /**
     * Returns whether the response was successful (no errors)
     * 
     * @return whether the response was successful
     */
    public boolean wasSuccessful() {
        return getErrors().length == 0;
    }

    /**
     * Get any errors returned in the response.
     * 
     * @return the response errors
     */
    public String[] getErrors() {
        return result.has(ERRORS) ? parseArray(ERRORS) : new String[0];
    }

    /**
     * Get any warnings returned in the response.
     * 
     * @return the response warnings
     */
    public String[] getWarnings() {
        return result.has(WARNINGS) ? parseArray(WARNINGS) : new String[0];
    }

    /**
     * Get the name of the root JSON result
     *
     * @return the root element name
     */
    protected abstract String getRoot();

    private String[] parseArray(String key) {
        List<String> elements = new ArrayList<String>();
        for (JsonElement j : result.getAsJsonArray(key)) {
            elements.add(j.getAsString());
        }
        return elements.toArray(new String[elements.size()]);
    }
}
