package com.rallydev.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URISyntaxException;

public class CollectionQueryExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        //(Server, username, password and proxy settings configured in Factory)
        RallyRestApi restApi = RestApiFactory.getRestApi();

        try {
            //Get a story with defects
            System.out.println("\nQuerying for stories with defects...");
            QueryRequest storiesWithDefects = new QueryRequest("hierarchicalrequirement");
            storiesWithDefects.setQueryFilter(new QueryFilter("Defects.ObjectID", "!=", null));
            storiesWithDefects.setFetch(new Fetch("FormattedID", "Name", "Defects"));
            QueryResponse storiesWithDefectsResponse = restApi.query(storiesWithDefects);
            JsonObject story = storiesWithDefectsResponse.getResults().get(0).getAsJsonObject();
            System.out.println(String.format("Found: %s - %s", story.get("FormattedID").getAsString(), story.get("Name").getAsString()));

            //Inspect the defects collection
            JsonObject defectInfo = story.getAsJsonObject("Defects");
            int defectCount = defectInfo.get("Count").getAsInt();
            System.out.println(String.format("\nTotal defects: %d", defectCount));

            //Query the defects collection
            QueryRequest defectRequest = new QueryRequest(defectInfo);
            defectRequest.setFetch(new Fetch("FormattedID", "Name", "State", "Priority"));

            QueryResponse queryResponse = restApi.query(defectRequest);
            if (queryResponse.wasSuccessful()) {
                for (JsonElement result : queryResponse.getResults()) {
                    JsonObject defect = result.getAsJsonObject();
                    System.out.println(String.format("\t%s - %s: Priority=%s, State=%s",
                            defect.get("FormattedID").getAsString(),
                            defect.get("Name").getAsString(),
                            defect.get("Priority").getAsString(),
                            defect.get("State").getAsString()));
                }
            }
        } finally {
            //Release resources
            restApi.close();
        }
    }
}
