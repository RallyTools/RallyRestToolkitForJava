package com.rallydev.rest.util;

/**
 * Represents a query filter to be applied to query requests.
 * <p>More on the WSAPI query syntax can be found here: http://rally1.rallydev.com/slm/doc/webservice</p>
 */
public class QueryFilter implements Cloneable {

    private String field;
    private String operator;
    private String value;

    private QueryFilter left = null;
    private QueryFilter right = null;

    /**
     * Create a new query filter with the specified options.
     *
     * @param field    the object field to evaluate
     * @param operator the operator to use for evaluation
     * @param value    the value to be evaluated
     */
    public QueryFilter(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Internal constructor for joining multiple QueryFilter objects by an AND or OR.
     *
     * @param left     the left query
     * @param operator AND/OR
     * @param right    the right query
     */
    protected QueryFilter(QueryFilter left, String operator, QueryFilter right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Get a query filter that is the ANDed combination of this filter and the specified one.
     *
     * @param q the filter to be ANDed
     * @return the ANDed query filter
     */
    public QueryFilter and(QueryFilter q) {
        return new QueryFilter(this, "AND", q);
    }

    /**
     * Get a query filter that is the ORed combination of this filter and the specified one.
     *
     * @param q the filter to be ORed
     * @return the ORed query filter
     */
    public QueryFilter or(QueryFilter q) {
        return new QueryFilter(this, "OR", q);
    }

    /**
     * Get the string representation of this query filter.
     * <p>Examples:</p>
     * <ul>
     * <li>(ScheduleState = Accepted)</li>
     * <li>((ScheduleState = Accepted) AND (Iteration.Name = "My Iteration"))</li>
     * </ul>
     *
     * @return the string representation of this query filter.
     */
    public String toString() {
        if (left != null) {
            return String.format("(%s %s %s)", left, operator, right);
        } else {
            String val = value;
            if (val != null) {
                val = val.contains(" ") ? "\"" + val + "\"" : val;

                if (Ref.isRef(val)) {
                    val = Ref.getRelativeRef(val);
                }
            }
            return String.format("(%s %s %s)", field, operator, val);
        }
    }

    /**
     * Get a query filter that is the ANDed combination of the specified filters.
     *
     * @param queryFilters one or more query filters to be ANDed together
     * @return the ANDed query filter
     */
    public static QueryFilter and(QueryFilter... queryFilters) {
        QueryFilter result = null;
        for (QueryFilter q : queryFilters) {
            result = result == null ? q : result.and(q);
        }
        return result;
    }

    /**
     * Get a query filter that is the ORed combination of the specified filters.
     *
     * @param queryFilters one or more query filters to be ORed together
     * @return the ORed query filter
     */
    public static QueryFilter or(QueryFilter... queryFilters) {
        QueryFilter result = null;
        for (QueryFilter q : queryFilters) {
            result = result == null ? q : result.or(q);
        }
        return result;
    }
}
