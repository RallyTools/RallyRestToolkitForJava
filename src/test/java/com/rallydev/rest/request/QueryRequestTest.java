package com.rallydev.rest.request;

import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;
import org.apache.http.client.utils.URLEncodedUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryRequestTest {
    
    @Test
    public void shouldCreateCorrectDefaultQuery() {
        
        QueryRequest q = new QueryRequest("Defect");
        
        Assert.assertEquals(q.getPageSize(), 200);
        Assert.assertEquals(q.getStart(), 1);
        Assert.assertEquals(q.getOrder(), "ObjectID");
        Assert.assertEquals(q.toUrl(), "/defect.js?start=1&pagesize=200&fetch=true&order=ObjectID");
    }

    @Test
    public void shouldCreateCorrectDefaultQueryWithExtraParam() {

        QueryRequest q = new QueryRequest("Defect");
        q.addParam("foo", "Bar");
        Assert.assertEquals(q.toUrl(), "/defect.js?foo=Bar&start=1&pagesize=200&fetch=true&order=ObjectID");
    }

    @Test
    public void shouldEncodeParamsUsingUtf8() {

        QueryRequest q = new QueryRequest("Defect");
        q.addParam("foo", "备");
        Assert.assertTrue(q.toUrl().contains(URLEncodedUtils.format(q.getParams(), "utf-8")));
    }

    @Test
    public void shouldCreateCorrectQueryWithFetch() {

        QueryRequest q = new QueryRequest("Defect");
        q.setFetch(new Fetch("Name", "Description"));
        Assert.assertTrue(q.toUrl().contains("fetch=Name%2CDescription"));
    }

    @Test
    public void shouldCreateCorrectQueryWithDefaultOrder() {

        QueryRequest q = new QueryRequest("Defect");
        Assert.assertTrue(q.toUrl().contains("order=ObjectID"));
    }

    @Test
    public void shouldCreateCorrectQueryWithSpecifiedOrder() {

        QueryRequest q = new QueryRequest("Defect");
        q.setOrder("Name");
        Assert.assertTrue(q.toUrl().contains("order=Name%2CObjectID"));
    }

    @Test
    public void shouldCreateCorrectQueryWithPageSize() {

        QueryRequest q = new QueryRequest("Defect");
        q.setPageSize(1);
        Assert.assertTrue(q.toUrl().contains("pagesize=1"));
    }

    @Test
    public void shouldCreateCorrectQueryWithStart() {

        QueryRequest q = new QueryRequest("Defect");
        q.setStart(50);
        Assert.assertTrue(q.toUrl().contains("start=50"));
    }

    @Test
    public void shouldCreateCorrectQueryWithQuery() {

        QueryRequest q = new QueryRequest("Defect");
        q.setQueryFilter(new QueryFilter("State", "=", "Fixed"));
        Assert.assertTrue(q.toUrl().contains("query=%28State+%3D+Fixed%29"));
    }

    @Test
    public void shouldCreateCorrectQueryWithWorkspace() {

        QueryRequest q = new QueryRequest("Defect");
        q.setWorkspace("/workspace/1234");
        Assert.assertTrue(q.toUrl().contains("workspace=%2Fworkspace%2F1234"));
    }

    @Test
     public void shouldCreateCorrectQueryWithProject() {

        QueryRequest q = new QueryRequest("Defect");
        q.setProject("/project/1234");
        Assert.assertTrue(q.toUrl().contains("project=%2Fproject%2F1234"));
        Assert.assertTrue(q.toUrl().contains("projectScopeUp=false"));
        Assert.assertTrue(q.toUrl().contains("projectScopeDown=true"));

        q.setScopedDown(false);
        q.setScopedUp(true);
        Assert.assertTrue(q.toUrl().contains("projectScopeUp=true"));
        Assert.assertTrue(q.toUrl().contains("projectScopeDown=false"));

    }

    @Test
    public void shouldCreateCorrectQueryWithNullProject() {

        QueryRequest q = new QueryRequest("Defect");
        q.setProject(null);
        Assert.assertTrue(q.toUrl().contains("project=null"));
        Assert.assertFalse(q.toUrl().contains("projectScopeUp"));
        Assert.assertFalse(q.toUrl().contains("projectScopeDown"));

    }

    @Test
    public void shouldCloneCorrectly() {

        QueryRequest q = new QueryRequest("Defect");
        q.setProject("/project/1234");
        q.setProject("/workspace/2345");
        q.setScopedDown(false);
        q.setScopedUp(true);
        q.setFetch(new Fetch("Name", "Description"));
        q.setQueryFilter(new QueryFilter("State", "=", "Fixed"));
        q.setPageSize(5);
        q.setOrder("Name");
        q.setStart(10);
        q.addParam("foo", "Bar");
        
        QueryRequest q2 = q.clone();
        Assert.assertEquals(q.toUrl(), q2.toUrl());

    }

    @Test
    public void shouldCreateCorrectUrlForSubscription() {

        QueryRequest q = new QueryRequest("Subscription");

        Assert.assertEquals(q.toUrl(), "/subscriptions.js?start=1&pagesize=200&fetch=true&order=ObjectID");
    }

    @Test
    public void shouldCreateCorrectUrlForUser() {

        QueryRequest q = new QueryRequest("User");

        Assert.assertEquals(q.toUrl(), "/users.js?start=1&pagesize=200&fetch=true&order=ObjectID");
    }
    
    @Test
    public void shouldCreateCorrectUrlForCollection() {
        JsonObject collection = new JsonObject();
        collection.addProperty("_ref", "/defect/1234/tasks");
        
        QueryRequest q = new QueryRequest(collection);
        Assert.assertEquals(q.toUrl(), "/defect/1234/tasks?start=1&pagesize=200&fetch=true&order=ObjectID");
    }
}
