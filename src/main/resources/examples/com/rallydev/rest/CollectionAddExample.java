package com.rallydev.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.request.CollectionUpdateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.CollectionUpdateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URISyntaxException;

public class CollectionAddExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        //(Server, username, password and proxy settings configured in Factory)
        RallyRestApi restApi = RestApiFactory.getRestApi();

        try {
            //Get a story without any tags
            System.out.println("\nQuerying for stories without tags...");
            QueryRequest storyRequest = new QueryRequest("hierarchicalrequirement");
            storyRequest.setQueryFilter(new QueryFilter("Tags.ObjectID", "=", "null"));
            storyRequest.setFetch(new Fetch("FormattedID", "Name", "Tags"));

            QueryResponse storyResponse = restApi.query(storyRequest);
            JsonObject story = storyResponse.getResults().get(0).getAsJsonObject();
            System.out.println(String.format("Found: %s - %s", story.get("FormattedID").getAsString(), story.get("Name").getAsString()));

            //Get a tag
            System.out.println("\nQuerying for tags...");
            QueryRequest tagsRequest = new QueryRequest("tag");
            tagsRequest.setFetch(new Fetch("Name"));
            QueryResponse tagsResponse = restApi.query(tagsRequest);
            JsonObject tag = tagsResponse.getResults().get(0).getAsJsonObject();
            System.out.println(String.format("Found: %s - %s", tag.get("Name").getAsString(), tag.get("_ref").getAsString()));

            //Add the tag to the collection
            JsonArray tagRefs = new JsonArray();
            JsonObject tagRef = new JsonObject();
            tagRef.addProperty("_ref", tag.get("_ref").getAsString());
            tagRefs.add(tagRef);

            //Update the collection
            CollectionUpdateRequest storyTagCollectionAddRequest = new CollectionUpdateRequest(story.getAsJsonObject("Tags"), tagRefs, true);
            storyTagCollectionAddRequest.setFetch(new Fetch("Name"));
            CollectionUpdateResponse storyTagCollectionAddResponse = restApi.updateCollection(storyTagCollectionAddRequest);
            if(storyTagCollectionAddResponse.wasSuccessful()) {
                for(JsonElement addedTag : storyTagCollectionAddResponse.getResults()) {
                    System.out.println(String.format("Added tag: %s - %s", tag.get("Name").getAsString(), addedTag.getAsJsonObject().get("_ref").getAsString()));
                }
            }
        } finally {
            //Release resources
            restApi.close();
        }
    }
}
