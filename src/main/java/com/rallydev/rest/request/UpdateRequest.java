package com.rallydev.rest.request;

import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.Ref;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a WSAPI request to update an object.
 */
public class UpdateRequest extends Request {

    private String ref;
    private JsonObject obj;
    private Fetch fetch = new Fetch();

    /**
     * Create a new update request for the specified object and values.
     * 
     * @param ref the ref of the WSAPI object to be created.  May be absolute or relative, e.g. "/defect/12345"
     * @param obj the JSON representation of the values of the object
     */
    public UpdateRequest(String ref, JsonObject obj) {
        this.ref = ref;
        this.obj = obj;
    }

    /**
     * Get the JSON encoded string representation of the object to be updated.
     *
     * @return the JSON encoded object
     */
    public String getBody() {
        JsonObject wrapper = new JsonObject();
        wrapper.add(Ref.getTypeFromRef(ref), obj);
        return gsonBuilder.create().toJson(wrapper);
    }

    /**
     * <p>Get the current list of fields to be returned on the updated object.</p>
     * By default all fields will be returned in the response (fetch=true).
     *
     * @return the current list of fields.
     */
    public Fetch getFetch() {
        return fetch;
    }

    /**
     * Set the current list of fields to be returned on the updated object.
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

        return String.format("%s.js?%s", Ref.getRelativeRef(ref),
                URLEncodedUtils.format(params, "utf-8"));
    }
}
