package com.rallydev.rest.request;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Base class for all WSAPI requests.</p>  
 * Subclasses classes should provide an implementation of {@link com.rallydev.rest.request.Request#toUrl}
 */
public abstract class Request {
    
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    /**
     * Create a new request.
     */
    public Request() {
    }

    /**
     * Get the list of additional parameters included in this request.
     * 
     * @return The list of additional parameters
     */
    public List<NameValuePair> getParams() {
        return params;
    }

    /**
     * Set the list of additional parameters included in this request.
     * 
     * @param params The list of additional parameters
     */
    public void setParams(List<NameValuePair> params) {
        this.params = params;
    }

    /**
     * Add the specified parameter to this request.
     * 
     * @param name the parameter name
     * @param value the parameter value
     */
    public void addParam(String name, String value) {
        getParams().add(new BasicNameValuePair(name, value));
    }

    /**
     * <p>Convert this request into a url compatible with the WSAPI.</p>
     * Must be implemented by subclasses.
     * 
     * @return the url representing this request.
     */
    public abstract String toUrl();
}
