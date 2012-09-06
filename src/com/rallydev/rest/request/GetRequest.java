package com.rallydev.rest.request;

import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.Ref;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a WSAPI request to retrieve a specific object.
 */
public class GetRequest extends Request {

    private String ref;
    private Fetch fetch = new Fetch();

    /**
     * Create a new get request for the specified object.
     * @param ref the ref of the WSAPI object to be retrieved.  May be absolute or relative, e.g. "/defect/12345"
     */
    public GetRequest(String ref) {
        this.ref = ref;
    }

    /**
     * <p>Get the current list of fields to be returned on the retrieved object.</p>
     * By default all fields will be returned in the response (fetch=true).
     *
     * @return the current list of fields.
     */
    public Fetch getFetch() {
        return fetch;
    }

    /**
     * Set the current list of fields to be returned on the retrieved object.
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
        params.add(new BasicNameValuePair("fetch", fetch.toString()));
        return String.format("%s.js?%s", Ref.getRelativeRef(ref),
                URLEncodedUtils.format(params, "utf-8"));
    }
}
