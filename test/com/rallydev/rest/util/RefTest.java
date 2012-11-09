package com.rallydev.rest.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RefTest {

    @Test
    public void shouldDetectValidRefs() {
        Assert.assertTrue(Ref.isRef("/defect/1234"), "Valid relative ref");
        Assert.assertTrue(Ref.isRef("/defect/1234.js"), "Valid relative ref w/ extension");
        Assert.assertTrue(Ref.isRef("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234"), "Valid absolute ref");
        Assert.assertTrue(Ref.isRef("http://rally1.rallydev.com/slm/webservice/1.32/defect/1234.js"), "Valid absolute ref w/ extension");
    }

    @Test
    public void shouldDetectValidDynatypeRefs() {
        Assert.assertTrue(Ref.isRef("/portfolioitem/feature/1234"), "Valid relative ref");
        Assert.assertTrue(Ref.isRef("/portfolioitem/feature/1234.js"), "Valid relative ref w/ extension");
        Assert.assertTrue(Ref.isRef("https://rally1.rallydev.com/slm/webservice/1.32/portfolioitem/feature/1234"), "Valid absolute ref");
        Assert.assertTrue(Ref.isRef("http://rally1.rallydev.com/slm/webservice/1.32/portfolioitem/feature/1234.js"), "Valid absolute ref w/ extension");
    }

    @Test
    public void shouldDetectInvalidRefs() {
        Assert.assertFalse(Ref.isRef("/defect"), "Invalid ref");
        Assert.assertFalse(Ref.isRef("https://rally1.rallydev.com/slm/webservice/1.32/defect/abc.js"), "Invalid ref");
        Assert.assertFalse(Ref.isRef(null), "A null ref");
        Assert.assertFalse(Ref.isRef(""), "An empty string");
    }

    @Test
    public void shouldReturnValidRelativeRefs() {
        Assert.assertEquals(Ref.getRelativeRef("/defect/1234"), "/defect/1234", "Already relative ref");
        Assert.assertEquals(Ref.getRelativeRef("/defect/1234.js"), "/defect/1234", "Already relative ref");
        Assert.assertEquals(Ref.getRelativeRef("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234"), "/defect/1234", "Absolute ref");
    }

    @Test
    public void shouldReturnValidDynatypeRelativeRefs() {
        Assert.assertEquals(Ref.getRelativeRef("/portfolioitem/feature/1234"), "/portfolioitem/feature/1234", "Already relative ref");
        Assert.assertEquals(Ref.getRelativeRef("/portfolioitem/feature/1234.js"), "/portfolioitem/feature/1234", "Already relative ref");
        Assert.assertEquals(Ref.getRelativeRef("https://rally1.rallydev.com/slm/webservice/1.32/portfolioitem/feature/1234"), "/portfolioitem/feature/1234", "Absolute ref");
    }

    @Test
    public void shouldReturnNullRelativeRefs() {
        Assert.assertNull(Ref.getRelativeRef("blah"), "Not a ref");
        Assert.assertNull(Ref.getRelativeRef(""), "Empty ref");
        Assert.assertNull(Ref.getRelativeRef(null), "null ref");
    }

    @Test
    public void shouldReturnTypesFromRefs() {
        Assert.assertEquals(Ref.getTypeFromRef("/defect/1234"), "defect", "Relative ref");
        Assert.assertEquals(Ref.getTypeFromRef("/defect/1234.js"), "defect", "Relative ref with extension");
        Assert.assertEquals(Ref.getTypeFromRef("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234"), "defect", "Valid absolute ref");
    }

    @Test
    public void shouldReturnTypesFromDynatypeRefs() {
        Assert.assertEquals(Ref.getTypeFromRef("/portfolioitem/feature/1234"), "portfolioitem/feature", "Relative ref");
        Assert.assertEquals(Ref.getTypeFromRef("/portfolioitem/feature/1234.js"), "portfolioitem/feature", "Relative ref with extension");
        Assert.assertEquals(Ref.getTypeFromRef("https://rally1.rallydev.com/slm/webservice/1.32/portfolioitem/feature/1234"), "portfolioitem/feature", "Valid absolute ref");
    }

    @Test
    public void shouldReturnNullTypesFromRefs() {
        Assert.assertNull(Ref.getTypeFromRef("blah"), "Not a ref");
        Assert.assertNull(Ref.getTypeFromRef(""), "Empty ref");
        Assert.assertNull(Ref.getTypeFromRef(null), "null ref");
    }

    @Test
    public void shouldReturnOidsFromRefs() {
        Assert.assertEquals(Ref.getOidFromRef("/defect/1234"), "1234", "Relative ref");
        Assert.assertEquals(Ref.getOidFromRef("/defect/1234.js"), "1234", "Relative ref with extension");
        Assert.assertEquals(Ref.getOidFromRef("https://rally1.rallydev.com/slm/webservice/1.32/defect/1234"), "1234", "Valid absolute ref");
    }

    @Test
    public void shouldReturnOidsFromDynatypeRefs() {
        Assert.assertEquals(Ref.getOidFromRef("/portfolioitem/feature/1234"), "1234", "Relative ref");
        Assert.assertEquals(Ref.getOidFromRef("/portfolioitem/feature/1234.js"), "1234", "Relative ref with extension");
        Assert.assertEquals(Ref.getOidFromRef("https://rally1.rallydev.com/slm/webservice/1.32/portfolioitem/feature/1234"), "1234", "Valid absolute ref");
    }

    @Test
    public void shouldReturnNullOidsFromRefs() {
        Assert.assertNull(Ref.getOidFromRef("blah"), "Not a ref");
        Assert.assertNull(Ref.getOidFromRef(""), "Empty ref");
        Assert.assertNull(Ref.getOidFromRef(null), "null ref");
    }

    @Test
    public void shouldSupportWsapiVersionXinRefs() {
        Assert.assertEquals(Ref.getRelativeRef("https://rally1.rallydev.com/slm/webservice/x/portfolioitem/feature/1234"), "/portfolioitem/feature/1234", "Valid absolute version x dynatype ref");
        Assert.assertEquals(Ref.getRelativeRef("https://rally1.rallydev.com/slm/webservice/x/defect/1234"), "/defect/1234", "Valid absolute version x ref");
    }

    @Test
    public void shouldSupportWorkspacePermissionRefs() {
        Assert.assertEquals(Ref.getRelativeRef("https://rally1.rallydev.com/slm/webservice/1.38/workspacepermission/123u456w1"), "/workspacepermission/123u456w1", "Valid workspace permission ref");
        Assert.assertEquals(Ref.getOidFromRef("/workspacepermission/123u456w1.js"), "123u456w1", "Get oid from workspace permission ref");
        Assert.assertEquals(Ref.getTypeFromRef("/workspacepermission/123u456w1.js"), "workspacepermission", "Get type from workspace permission ref");
    }

    @Test
    public void shouldSupportProjectPermissionRefs() {
        Assert.assertEquals(Ref.getRelativeRef("https://rally1.rallydev.com/slm/webservice/1.38/projectpermission/123u456p1"), "/projectpermission/123u456p1", "Valid project permission ref");
        Assert.assertEquals(Ref.getOidFromRef("/projectpermission/123u456p1.js"), "123u456p1", "Get oid from project permission ref");
        Assert.assertEquals(Ref.getTypeFromRef("/projectpermission/123u456p1.js"), "projectpermission", "Get type from project permission ref");
    }
}
