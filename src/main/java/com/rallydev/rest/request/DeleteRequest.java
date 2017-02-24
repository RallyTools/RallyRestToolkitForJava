package com.rallydev.rest.request;

import com.rallydev.rest.util.Ref;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * Represents a WSAPI request to delete an object.
 */
public class DeleteRequest extends Request{

    private String ref;

    /**
     * Createa new delete request for the specified object.
     * @param ref the ref of the WSAPI object to be created.  May be absolute or relative, e.g. "/defect/12345"
     */
    public DeleteRequest(String ref) {
        this.ref = ref;
    }

    /**
     * <p>Convert this request into a url compatible with the WSAPI.</p>
     * Any parameters set will be included.
     *
     * @return the url representing this request.
     */
    @Override
    public String toUrl() {
        return String.format("%s.js%s", Ref.getRelativeRef(ref),
                getParams().size() > 0 ? ("?" + URLEncodedUtils.format(getParams(), "utf-8")) : "");
    }
}
