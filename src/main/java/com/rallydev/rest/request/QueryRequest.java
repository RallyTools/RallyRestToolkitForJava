package com.rallydev.rest.request;

import com.google.gson.JsonObject;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;
import com.rallydev.rest.util.Ref;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a WSAPI request to retrieve all objects matching specified criteria.
 */
public class QueryRequest extends Request implements Cloneable {

    private String type;
    private JsonObject collection;

    private Fetch fetch = new Fetch();
    private String order = "ObjectID";
    private QueryFilter queryFilter = null;

    private int pageSize = 200;
    private int limit = 0;
    private int start = 1;

    private String workspace = "";
    private String project = "";
    private boolean scopedUp = false;
    private boolean scopedDown = true;

    /**
     * Create a new query request for the specified type.
     *
     * @param type The WSAPI type to be queried, e.g. Defect
     */
    public QueryRequest(String type) {
        this.type = type;
    }

    /**
     * Create a new query request for the specified collection.
     * Only supported in WSAPI v2.0 and above.
     *
     * @param collection The collection to query.  Should have a _ref property.
     */
    public QueryRequest(JsonObject collection) {
        this.collection = collection;
    }

    /**
     * Get the filter by which the result set will be narrowed down.
     *
     * @return the filter
     */
    public QueryFilter getQueryFilter() {
        return queryFilter;
    }

    /**
     * Set a filter by which the result set will be narrowed down.
     *
     * @param queryFilter the filter
     */
    public void setQueryFilter(QueryFilter queryFilter) {
        this.queryFilter = queryFilter;
    }

    /**
     * Set the order by which the result set will be sorted.
     * <p>The default is ObjectID ASC.</p>
     *
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * Get the order by which the result set will be sorted.
     *
     * @param order the order
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * Get the workspace which the result set should be scoped to.
     *
     * @return the project
     */
    public String getWorkspace() {
        return workspace;
    }

    /**
     * <p>Specify the workspace which the result set should be scoped to.</p>
     * The default is the user's default workspace.
     *
     * @param workspaceRef the ref of the workspace to scope to.  May be an absolute or relative ref, e.g. /workspace/1234
     */
    public void setWorkspace(String workspaceRef) {
        this.workspace = workspaceRef;
    }

    /**
     * Get the project which the result set should be scoped to.
     *
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * <p>Specify the project which the result set should be scoped to.</p>
     * The default is the user's default project.
     * Specifying null will cause the result to be scoped to the entire specified workspace.
     *
     * @param projectRef the ref of the project to scope to.  May be null or an absolute or relative ref, e.g. /project/1234
     */
    public void setProject(String projectRef) {
        this.project = projectRef;
    }

    /**
     * If a project has been specified, get whether to include matching objects in parent projects in the result set.
     *
     * @return whether to include matching objects in parent projects.
     */
    public boolean isScopedUp() {
        return scopedUp;
    }

    /**
     * <p>If a project has been specified, set whether to include matching objects in parent projects in the result set.</p>
     * Defaults to false.
     *
     * @param scopeUp whether to include matching objects in parent projects
     */
    public void setScopedUp(boolean scopeUp) {
        this.scopedUp = scopeUp;
    }

    /**
     * If a project has been specified, get whether to include matching objects in child projects in the result set.
     *
     * @return whether to include matching objects in child projects.
     */
    public boolean isScopedDown() {
        return scopedDown;
    }

    /**
     * <p>If a project has been specified, set whether to include matching objects in child projects in the result set.</p>
     * Defaults to true.
     *
     * @param scopeDown whether to include matching objects in child projects
     */
    public void setScopedDown(boolean scopeDown) {
        this.scopedDown = scopeDown;
    }

    /**
     * Get the start index of the result set.
     *
     * @return the start index
     */
    public int getStart() {
        return start;
    }

    /**
     * Set the 1-based start index of the result set.
     * The default is 1.
     *
     * @param start the start index
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * <p>Get the current list of fields to be returned on the matching objects.</p>
     * By default all fields will be returned in the response (fetch=true).
     *
     * @return the current list of fields.
     */
    public Fetch getFetch() {
        return fetch;
    }

    /**
     * Set the current list of fields to be returned on the matching objects.
     *
     * @param fetch the list of fields to be returned.
     */
    public void setFetch(Fetch fetch) {
        this.fetch = fetch;
    }

    /**
     * Get the page size of the result set.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * <p>Set the page size of the result set.</p>
     * The default is 200.
     *
     * @param pageSize the new page size.  Must be between 1 and 200 inclusive.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Get the maximum number of records to be returned from the query.
     *
     * @return the maximum number of records
     */
    public int getLimit() {
        return limit;
    }

    /**
     * <p>Set the maximum number of records to be returned from the query.</p>
     * If not set only one page of data will be returned by {@link com.rallydev.rest.RallyRestApi#query}
     *
     * @param limit the maximum number of records to be returned
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Clone this request.
     *
     * @return the cloned instance of this request.
     */
    @Override
    public QueryRequest clone() {
        try {
            return (QueryRequest) super.clone();
        } catch (CloneNotSupportedException c) {
            return null;
        }
    }

    /**
     * <p>Convert this request into a url compatible with the WSAPI.</p>
     * The current criteria set on this request and any other parameters will be included.
     *
     * @return the url representing this request.
     */
    @Override
    public String toUrl() {
        List<NameValuePair> params = new ArrayList<NameValuePair>(getParams());

        params.add(new BasicNameValuePair("start", Integer.toString(getStart())));
        params.add(new BasicNameValuePair("pagesize", Integer.toString(getPageSize())));
        params.add(new BasicNameValuePair("fetch", fetch.toString()));

        String order = getOrder();
        if (!order.contains("ObjectID")) {
            order += ",ObjectID";
        }
        params.add(new BasicNameValuePair("order", order));
        if (getQueryFilter() != null) {
            params.add(new BasicNameValuePair("query", getQueryFilter().toString()));
        }

        if (getWorkspace() != null && getWorkspace().length() > 0) {
            params.add(new BasicNameValuePair("workspace", Ref.getRelativeRef(getWorkspace())));
        }

        if (getProject() == null) {
            params.add(new BasicNameValuePair("project", "null"));
        } else if (getProject().length() > 0) {
            params.add(new BasicNameValuePair("project", getProject()));
            params.add(new BasicNameValuePair("projectScopeUp", Boolean.toString(isScopedUp())));
            params.add(new BasicNameValuePair("projectScopeDown", Boolean.toString(isScopedDown())));
        }

        return (this.type != null ? getTypeEndpoint() :
                Ref.getRelativeRef(collection.get("_ref").getAsString())) +
                "?" + URLEncodedUtils.format(params, "utf-8");
    }

    protected String getTypeEndpoint() {
        String typeEndpoint = type.toLowerCase().replaceAll(" ", "");
        if (typeEndpoint.equals("subscription") || typeEndpoint.equals("user")) {
            typeEndpoint += "s";
        }
        return "/" + typeEndpoint + ".js";
    }
}