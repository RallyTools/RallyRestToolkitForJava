package com.rallydev.rest;

import com.google.gson.JsonObject;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.DeleteRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.DeleteResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Ref;

import java.io.IOException;
import java.net.URISyntaxException;

public class CrudExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        //(Server, username, password and proxy settings configured in Factory)
        RallyRestApi restApi = RestApiFactory.getRestApi();
        
        try {

            //Create a defect
            System.out.println("Creating defect...");
            JsonObject newDefect = new JsonObject();
            newDefect.addProperty("Name", "Test Defect");
            CreateRequest createRequest = new CreateRequest("defect", newDefect);
            CreateResponse createResponse = restApi.create(createRequest);
            System.out.println(String.format("Created %s", createResponse.getObject().get("_ref").getAsString()));

            //Read defect
            String ref = Ref.getRelativeRef(createResponse.getObject().get("_ref").getAsString());
            System.out.println(String.format("\nReading defect %s...", ref));
            GetRequest getRequest = new GetRequest(ref);
            GetResponse getResponse = restApi.get(getRequest);
            JsonObject obj = getResponse.getObject();
            System.out.println(String.format("Read defect. Name = %s, State = %s",
                    obj.get("Name").getAsString(), obj.get("State").getAsString()));

            //Update defect
            System.out.println("\nUpdating defect state...");
            JsonObject updatedDefect = new JsonObject();
            updatedDefect.addProperty("State", "Fixed");
            UpdateRequest updateRequest = new UpdateRequest(ref, updatedDefect);
            UpdateResponse updateResponse = restApi.update(updateRequest);
            obj = updateResponse.getObject();
            System.out.println(String.format("Updated defect. State = %s", obj.get("State").getAsString()));

            //Delete defect
            System.out.println("\nDeleting defect...");
            DeleteRequest deleteRequest = new DeleteRequest(ref);
            DeleteResponse deleteResponse = restApi.delete(deleteRequest);
            if (deleteResponse.wasSuccessful()) {
                System.out.println("Deleted defect.");
            }

        } finally {
            //Release all resources
            restApi.close();
        }
    }
}
