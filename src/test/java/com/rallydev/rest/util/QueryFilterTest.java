package com.rallydev.rest.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryFilterTest {
    
    @Test
    public void shouldCreateCorrectQueryForString() {
        QueryFilter q = new QueryFilter("Foo", "=", "Bar");
        Assert.assertEquals(q.toString(), "(Foo = Bar)", "String value query correct");    
    }

    @Test
    public void shouldCreateCorrectQueryForNull() {
        QueryFilter q = new QueryFilter("Foo", "=", null);
        Assert.assertEquals(q.toString(), "(Foo = null)", "Null value query correct");
    }

    @Test
     public void shouldCreateQuotedQueryForStringWithSpaces() {
        QueryFilter q = new QueryFilter("Foo", "=", "Bar Baz");
        Assert.assertEquals(q.toString(), "(Foo = \"Bar Baz\")", "String value with spaces query correct");
    }

    @Test
    public void shouldCreateCorrectQueryForRef() {
        String relativeRef = "/hierarchicalrequirement/1234";
        QueryFilter q = new QueryFilter("Parent", "=", relativeRef);
        QueryFilter q2 = new QueryFilter("Parent", "=", "https://rally1.rallydev.com/slm/webservice/1.32" + relativeRef);
        Assert.assertEquals(q.toString(), "(Parent = " + relativeRef + ")", "Relative ref value correct");
        Assert.assertEquals(q2.toString(), "(Parent = " + relativeRef + ")", "Absolute ref value correct");
    }

    @Test
    public void shouldCreateCorrectQueryForNumber() {
        QueryFilter q = new QueryFilter("Foo", ">", "6");
        Assert.assertEquals(q.toString(), "(Foo > 6)", "Numeric value with spaces query correct");
    }

    @Test
    public void shouldCreateCorrectAndedQuery() {
        QueryFilter q = new QueryFilter("Foo", "=", "Bar");
        QueryFilter q2 = q.and(new QueryFilter("Bar", "=", "Baz"));
        Assert.assertEquals(q2.toString(), "((Foo = Bar) AND (Bar = Baz))");
    }

    @Test
    public void shouldCreateCorrectStaticAndedQuery() {
        QueryFilter q = new QueryFilter("Foo", "=", "Bar");
        QueryFilter q2 = new QueryFilter("Bar", "=", "Baz");
        QueryFilter q3 = new QueryFilter("Baz", "=", "Foo");
        Assert.assertEquals(QueryFilter.and(q, q2, q3).toString(), "(((Foo = Bar) AND (Bar = Baz)) AND (Baz = Foo))");
        Assert.assertNull(QueryFilter.and());
    }

    @Test
    public void shouldCreateCorrectOredQuery() {
        QueryFilter q = new QueryFilter("Foo", "=", "Bar");
        QueryFilter q2 = q.or(new QueryFilter("Bar", "=", "Baz"));
        Assert.assertEquals(q2.toString(), "((Foo = Bar) OR (Bar = Baz))");
    }

    @Test
    public void shouldCreateCorrectStaticOredQuery() {
        QueryFilter q = new QueryFilter("Foo", "=", "Bar");
        QueryFilter q2 = new QueryFilter("Bar", "=", "Baz");
        QueryFilter q3 = new QueryFilter("Baz", "=", "Foo");
        Assert.assertEquals(QueryFilter.or(q, q2, q3).toString(), "(((Foo = Bar) OR (Bar = Baz)) OR (Baz = Foo))");
        Assert.assertNull(QueryFilter.or());
    }
}
