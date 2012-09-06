package com.rallydev.rest.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.Ref;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a WSAPI request to create an object.
 */
public class CreateRequest extends Request {

    private String type;
    private JsonObject obj;
    private Fetch fetch = new Fetch();

    /**
     * Create a new create request with the specified type and values.
     * 
     * @param type the WSAPI object type to be created, e.g. Defect
     * @param obj the JSON representation of the values of the object
     */
    public CreateRequest(String type, JsonObject obj) {
        this.type = type;
        this.obj = obj;
    }

    /**
     * Get the JSON encoded string representation of the object to be created.
     * 
     * @return the JSON encoded object
     */
    public String getBody() {
        JsonObject wrapper = new JsonObject();
        wrapper.add(type, obj);
        return new Gson().toJson(wrapper);
    }

    /**
     * <p>Get the current list of fields to be returned on the created object.</p>
     * By default only the underscore fields such as _ref will be returned in the response.
     * 
     * @return the current list of fields.
     */
    public Fetch getFetch() {
        return fetch;
    }

    /**
     * Set the current list of fields to be returned on the created object.
     * 
     * @param fetch the list of fields to be returned.
     */
    public void setFetch(Fetch fetch) {
        this.fetch = fetch;
    }

    /**
     * <p>Convert this request into a url compatible with the WSAPI.</p>
     * The current fetch and any other parameters will be included.
     *
     * @return the url representing this request.
     */
    @Override
    public String toUrl() {
        List<NameValuePair> params = new ArrayList<NameValuePair>(getParams());

        params.add(new BasicNameValuePair("fetch", getFetch().toString()));

        return String.format("/%s/create.js?%s", type.toLowerCase().replaceAll(" ", ""),
                URLEncodedUtils.format(params, "utf-8"));
    }
}
