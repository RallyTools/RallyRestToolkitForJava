package com.rallydev.rest.request;

import com.google.gson.GsonBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class RequestTest {
    
    private Request createRequest() {
        return new Request() {
            @Override
            public String toUrl() {
                return "";
            }
        };    
    }
    
    @Test
    public void shouldBeAbleToAddParams() {
        Request r = createRequest();
        Assert.assertEquals(r.getParams().size(), 0);
        
        r.addParam("Name", "Value");
        Assert.assertEquals(r.getParams().size(), 1);
        
        r.addParam("Name2", "Value2");
        Assert.assertEquals(r.getParams().size(), 2);
        
        r.getParams().clear();
        Assert.assertEquals(r.getParams().size(), 0);
    }

    @Test
    public void shouldBeAbleToSetParams() {
        Request r = createRequest();
        Assert.assertEquals(r.getParams().size(), 0);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Name", "Value"));
        r.setParams(params);
        Assert.assertSame(params, r.getParams());
        Assert.assertEquals(r.getParams().size(), 1);
    }

    @Test
    public void shouldBeAbleToSetGsonBuilder() {
        Request r = createRequest();
        Assert.assertEquals(r.getParams().size(), 0);

        GsonBuilder previous = r.getGsonBuilder();
        GsonBuilder brandNew = new GsonBuilder();

        r.setGsonBuilder(brandNew);

        Assert.assertNotNull(r.getGsonBuilder());
        Assert.assertSame(r.getGsonBuilder(), brandNew);
        Assert.assertNotSame(brandNew, previous);
    }
}
