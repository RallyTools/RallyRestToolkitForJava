package com.rallydev.rest.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * Represents a WSAPI request to bulk provision a user.
 */
public class BulkUserPermissionRequest extends Request {

    private static final String PROJECTPERMISSION_BULKUPDATE = "/projectpermission/bulkupdate";

    private String userOID;
    private Collection<String> excludedProjectOIDs;
    private String rootProjectOID;
    private String permission;
    private boolean forceDowngradePermissions;

    /**
     * 
     * @param userOID
     *            The OID (ObjectID) of the User who will be granted new project permissions
     * @param excludedRootProjectOIDs
     *            The OIDs of any child Project (or a child of a child, or any ancestor to any level) under the root project which are to be excluded from the
     *            permission change operation.
     * @param rootProjectOID
     *            The OID of the root of the Project tree of which to change the permissions for the given user.  The user's Project permission for all Projects
     *            rooted at this one will be changed, unless (see below for further explanation)
     *            the Project is on the exclusions list, or
     *            the operation would result in a downgrade but the force downgrade parameters was not set to tree.
     * @param permission
     *            The permission to grant.  Must be one of No Access, Viewer, Editor, or Project Admin.
     * @param forceDowngradePermissions
     *            If you intend to downgrade any existing project permissions, set this to true.
     */
    public BulkUserPermissionRequest(String userOID, Collection<String> excludedProjectOIDs, String rootProjectOID, String permission,
            boolean forceDowngradePermissions) {
        super();
        this.userOID = userOID;
        this.excludedProjectOIDs = excludedProjectOIDs;
        this.rootProjectOID = rootProjectOID;
        this.permission = permission;
        this.forceDowngradePermissions = forceDowngradePermissions;
    }

    @Override
    public String toUrl() {
        List<NameValuePair> params = new ArrayList<NameValuePair>(getParams());
        params.add(new BasicNameValuePair("userOID", userOID));
        params.add(new BasicNameValuePair("rootProjectOID", rootProjectOID));
        params.add(new BasicNameValuePair("permission", permission));
        params.add(new BasicNameValuePair("forceDowngradePermissions", Boolean.toString(forceDowngradePermissions)));

        if (excludedProjectOIDs != null && !excludedProjectOIDs.isEmpty()) {
            params.add(new BasicNameValuePair("excludedRootProjectOIDs", String.join(",", excludedProjectOIDs)));
        }

        return String.format("%s?%s", PROJECTPERMISSION_BULKUPDATE, URLEncodedUtils.format(params, "utf-8"));
    }

}
