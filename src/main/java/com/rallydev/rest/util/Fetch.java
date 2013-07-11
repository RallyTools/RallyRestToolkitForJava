package com.rallydev.rest.util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a list of fields to be returned in responses from the WSAPI.
 */
public class Fetch extends ArrayList<String> {

    /**
     * Create a new fetch with the specified fields.
     * 
     * @param fetch one or more fields to be returned
     */
    public Fetch(String... fetch) {
        this.addAll(Arrays.asList(fetch));    
    }

    /**
     * Get the comma separated list of fields to be returned.
     * If the list is empty true will be returned.
     * 
     * @return the comma separated list of fields to be returned
     */
    @Override
    public String toString() {
        if (size() == 0) {
            return "true";
        } else {
            StringBuilder s = new StringBuilder();
            for (String f : this) {
                s.append(f);
                s.append(",");
            }
            s.deleteCharAt(s.length() - 1);
            return s.toString();
        }
    }
}
