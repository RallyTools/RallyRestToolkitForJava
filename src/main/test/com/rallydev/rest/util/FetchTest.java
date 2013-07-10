package com.rallydev.rest.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FetchTest {
    
    @Test
    public void shouldProvideCorrectDefaultFetch() {
        Fetch f = new Fetch();
        Assert.assertEquals(f.toString(), "true");
    }

    @Test
    public void shouldProvideCorrectFetch() {
        Fetch f = new Fetch("Name", "Description", "ScheduleState");
        Assert.assertEquals(f.toString(), "Name,Description,ScheduleState");
    }

    @Test
    public void shouldBeAbleToAddRemoveFetch() {
        Fetch f = new Fetch("Name", "Description", "ScheduleState");
        String fetch = f.toString();
        f.add("Foo");
        Assert.assertEquals(f.toString(), fetch + ",Foo");
        f.remove("Foo");
        Assert.assertEquals(f.toString(), fetch);
        f.clear();
        Assert.assertEquals(f.toString(), "true");
    }
}
