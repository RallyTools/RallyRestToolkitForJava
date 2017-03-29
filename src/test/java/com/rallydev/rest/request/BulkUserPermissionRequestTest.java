package com.rallydev.rest.request;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BulkUserPermissionRequestTest {

    @Test
    public void shouldConstructCorrectURL_NoExcludes_Viewer() {
        BulkUserPermissionRequest req = new BulkUserPermissionRequest("1234", null, "12345", "Viewer", false);
        Assert.assertEquals(req.toUrl(), "/projectpermission/bulkupdate?userOID=1234&rootProjectOID=12345&permission=Viewer&forceDowngradePermissions=false");
    }

    @Test
    public void shouldConstructCorrectURL_NoExcludes_Editor() {
        BulkUserPermissionRequest req = new BulkUserPermissionRequest("1234", null, "12345", "Editor", false);
        Assert.assertEquals(req.toUrl(), "/projectpermission/bulkupdate?userOID=1234&rootProjectOID=12345&permission=Editor&forceDowngradePermissions=false");
    }

    @Test
    public void shouldConstructCorrectURL_Editor() {
        BulkUserPermissionRequest req = new BulkUserPermissionRequest("1234", Arrays.asList("1122", "1133"), "12345", "Editor", false);
        Assert.assertEquals(req.toUrl(),
                "/projectpermission/bulkupdate?userOID=1234&rootProjectOID=12345&permission=Editor&forceDowngradePermissions=false&excludedRootProjectOIDs=1122%2C1133");
    }
}
