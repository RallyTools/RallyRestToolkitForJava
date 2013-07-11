package com.rallydev.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class CollectionQueryExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        //(Server, username, password and proxy settings configured in Factory)
        RallyRestApi restApi = RestApiFactory.getRestApi();

        try {
            String ref = "/hierarchicalrequirement/12544729477";
            GetRequest getRequest = new GetRequest(ref);
            getRequest.setFetch(new Fetch("Defects"));
            
            System.out.println(String.format("\nReading defect info for story %s...", ref));
            GetResponse getResponse = restApi.get(getRequest);
            JsonObject story = getResponse.getObject();
            
            JsonObject defectInfo = story.getAsJsonObject("Defects");
            int defectCount = defectInfo.get("Count").getAsInt();
            System.out.println(String.format("\nTotal defects: %d", defectCount));

            if(defectCount > 0) {
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
            }
        } finally {
            //Release resources
            restApi.close();
        }
    }
}
