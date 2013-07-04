package com.rallydev.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;
import com.rallydev.rest.util.Ref;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class CollectionSummaryExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        //(Server, username, password and proxy settings configured in Factory)
        RallyRestApi restApi = RestApiFactory.getRestApi();

        try {
            String ref = "/hierarchicalrequirement/12544729477";
            GetRequest getRequest = new GetRequest(ref);
            getRequest.setFetch(new Fetch("Defects:summary[Priority;State]"));
            
            System.out.println(String.format("\nSummarizing defects on story %s...", ref));
            GetResponse getResponse = restApi.get(getRequest);
            JsonObject story = getResponse.getObject();
            JsonObject defectSummary = story.getAsJsonObject("Summary").getAsJsonObject("Defects");
            
            System.out.println(String.format("\nTotal defects: %d", defectSummary.get("Count").getAsInt()));
            
            System.out.println("\nBy Priority:");
            JsonObject prioritySummary = defectSummary.getAsJsonObject("Priority");
            for(Map.Entry<String, JsonElement> summaryItem : prioritySummary.entrySet()) {
                System.out.println(String.format("\t%s - %d", summaryItem.getKey(), summaryItem.getValue().getAsInt()));    
            }

            System.out.println("\nBy State:");
            JsonObject stateSummary = defectSummary.getAsJsonObject("State");
            for(Map.Entry<String, JsonElement> summaryItem : stateSummary.entrySet()) {
                System.out.println(String.format("\t%s - %d", summaryItem.getKey(), summaryItem.getValue().getAsInt()));
            }

        } finally {
            //Release resources
            restApi.close();
        }
    }
}
