package com.rallydev.rest.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility methods for working with ref URLs.
 */
public class Ref {

    private static List<Pattern> patterns = new ArrayList<Pattern>(Arrays.asList(

            //dynatype collection ref (/portfolioitem/feature/1234
            Pattern.compile(".*?/(\\w{2,}/\\w+)/(\\d+/\\w+)(?:\\.js\\??.*)*$"),
            
            //dynatype ref (/portfolioitem/feature/1234
            Pattern.compile(".*?/(\\w{2,}/\\w+)/(\\d+)(?:\\.js\\??.*)*$"),

            //collection ref (/defect/1234/tasks)
            Pattern.compile(".*?/(\\w+/-?\\d+)/(\\w+)(?:\\.js\\??.*)*$"),
            
            //basic ref (/defect/1234)
            Pattern.compile(".*?/(\\w+)/(\\d+)(?:\\.js\\??.*)*$"),
            
            //permission ref (/workspacepermission/123u456w1)
            Pattern.compile(".*?/(\\w+)/(\\d+u\\d+[pw]\\d+)(?:\\.js\\??.*)*$")
    ));
    
    private static Matcher match(String ref) {
        String test = ref != null ? ref : "";
        for(Pattern pattern : patterns) {
            Matcher m = pattern.matcher(test);
            if(m.matches()) {
                return m;
            }
        }
        return null;
    }

    /**
     * Determine whether the specified string is a valid ref url.
     * 
     * @param ref the string to be tested.  May be either absolute or relative, e.g. /defect/1234
     * 
     * @return whether the specified string is a valid ref url
     */
    public static boolean isRef(String ref) {
        return match(ref) != null;
    }

    /**
     * Create a relative ref url from the specified ref
     * 
     * @param ref the ref url to be made relative
     * 
     * @return the relative ref url or null if the specified ref was not valid
     */
    public static String getRelativeRef(String ref) {
        Matcher matcher = match(ref);
        return matcher != null ? String.format("/%s/%s", matcher.group(1), matcher.group(2)) : null;
    }

    /**
     * Get the type from the specified ref url
     * 
     * @param ref the ref url to extract the type from
     *            
     * @return the extracted type or null if the specified ref was not valid
     */
    public static String getTypeFromRef(String ref) {
        Matcher matcher = match(ref);
        return matcher != null ? matcher.group(1) : null;
    }

    /**
     * Get the ObjectID from the specified ref url
     * 
     * @param ref the ref url to extract the ObjectID from
     *            
     * @return the extracted ObjectID or null if the specified ref was not valid
     */
    public static String getOidFromRef(String ref) {
        Matcher matcher = match(ref);
        return matcher != null ? matcher.group(2) : null;
    }
}
