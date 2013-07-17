## License

Copyright (c) Rally Software Development Corp. 2013 Distributed under the MIT License.

## Warranty

The Java Toolkit for Rally REST API is available on an as-is basis. 

## Support

Rally Software does not actively maintain this toolkit.  If you have a question or problem, we recommend posting it to Stack Overflow: http://stackoverflow.com/questions/ask?tags=rally 

##Introduction

The Java Toolkit for Rally REST API provides an intuitive Java API for accessing your Rally Data.

It provides a rich set of capabilities for querying, along with methods for creating, reading, updating, and deleting individual items.

[Download](https://github.com/RallyTools/RallyRestToolkitForJava/releases/download/v2.0.2/rally-rest-api-2.0.2.jar)

[Full API documentation](http://rallytools.github.io/RallyRestToolkitForJava/)

[Web Services API documentation](https://rally1.rallydev.com/slm/doc/webservice)

## Setup

Create a new Java project in your favorite IDE.

#### Manual
Add [rally-rest-api-2.0.2.jar](https://github.com/RallyTools/RallyRestToolkitForJava/releases/download/v2.0.2/rally-rest-api-2.0.2.jar) appropriate to your version of JDK to your classpath.

You will also need to add the following jars:

*   httpcore-4.2.4.jar
*   httpclient-4.2.5.jar
*   commons-logging-1.1.1.jar
*   commons-codec-1.6.jar
*   gson-2.2.4.jar

All the jars except gson-2.1.jar can be found in [httpcomponents-client-4.2.5-bin.zip](http://archive.apache.org/dist/httpcomponents/httpclient/binary/httpcomponents-client-4.2.5-bin.zip) in the archives for the Apache httpcomponents project.

The gson-2.2.4.jar file can be found in [google-gson-2.2.4-release.zip](http://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip) on Google Code.

#### Managed (Maven)
Add the rally-rest-api dependency to your pom.xml

```xml
<dependency>
    <groupId>com.rallydev.rest</groupId>
    <artifactId>rally-rest-api</artifactId>
    <version>2.0.2</version>
</dependency>
```

## Usage

In your main method, instantiate a new [RallyRestApi](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html):

<java>RallyRestApi restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), "user@company.com", "password");
</java>

<p>The parameters for [RallyRestApi](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html) are as follows:

<table><tbody>
<tr>
<th>Parameter</th>
<th>Description</th>
<th>Example</th>
</tr>
<tr>
<td>server*</td>
<td>The Rally server to connect to.</td>
<td><font face= Courier, Courier New, monospace><font color ="#0000FF">"https://rally1.rallydev.com"</font></font></td>
</tr>
<tr>
<td>userName*</td>
<td>The username to connect to Rally with.</td>
<td><font face= Courier, Courier New, monospace><font color ="#0000FF">"user@company.com"</font></font></td>
</tr>
<tr>
<td>password*</td>
<td>The password to connect to Rally with.</td>
<td><font face= Courier, Courier New, monospace><font color ="#0000FF">"password"</font></font></td>
</tr>
</tbody></table>
&nbsp; * = required parameter

## <a name="Java-PublicMethods"></a>Public Methods

[RallyRestApi](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html) exposes the following public methods:

<table style="table-layout: fixed">
<tbody>
<tr>
<th>Method Name</th>
<th>Parameters</th>
<th>Description</th>
</tr>

<tr>
<td>[create](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#create(com.rallydev.rest.request.CreateRequest))</td>
<td>[CreateRequest](https://docs.rallydev.com/javarestapi/com/rallydev/rest/request/CreateRequest.html) request*</td>
<td>Create the object described by the request parameter.  Returns a [CreateResponse](https://docs.rallydev.com/javarestapi/com/rallydev/rest/response/CreateResponse.html) object containing the results of the request.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    JsonObject newDefect = new JsonObject();
    newDefect.addProperty("Name", "Test Defect");
    CreateRequest createRequest = new CreateRequest("defect", newDefect);
    CreateResponse createResponse = restApi.create(createRequest);
</java>
</td>
</tr>

<tr>
<td>[get](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#get(com.rallydev.rest.request.GetRequest))</td>
<td>[GetRequest](https://docs.rallydev.com/javarestapi/com/rallydev/rest/request/GetRequest.html) request*</td>
<td>Retrieve the object described by the request parameter.  Returns a [GetResponse](https://docs.rallydev.com/javarestapi/com/rallydev/rest/response/GetResponse.html) object containing the results of the request.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    GetRequest getRequest = new GetRequest("/defect/1234.js");
    GetResponse getResponse = restApi.get(getRequest);
</java>
</td>
</tr>

<tr>
<td>[update](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#update(com.rallydev.rest.request.UpdateRequest))</td>
<td>[UpdateRequest](https://docs.rallydev.com/javarestapi/com/rallydev/rest/request/UpdateRequest.html) request*</td>
<td>Update the object described by the request parameter.  Returns a [UpdateResponse](https://docs.rallydev.com/javarestapi/com/rallydev/rest/response/UpdateResponse.html) object containing the results of the request.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    JsonObject updatedDefect = new JsonObject();
    updatedDefect.addProperty("State", "Fixed");
    UpdateRequest updateRequest = new UpdateRequest("/defect/1234.js", updatedDefect);
    UpdateResponse updateResponse = restApi.update(updateRequest);
</java>
</td>
</tr>

<tr>
<td>[delete](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#delete(com.rallydev.rest.request.DeleteRequest))</td>
<td>[DeleteRequest](https://docs.rallydev.com/javarestapi/com/rallydev/rest/request/DeleteRequest.html) request*</td>
<td>Delete the object described by the request parameter.  Returns a [DeleteResponse](https://docs.rallydev.com/javarestapi/com/rallydev/rest/response/DeleteResponse.html) object containing the results of the request.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    DeleteRequest deleteRequest = new DeleteRequest("/defect/1234.js");
    DeleteResponse deleteResponse = restApi.delete(deleteRequest);
</java>
</td>
</tr>

<tr>
<td>[query](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#query(com.rallydev.rest.request.QueryRequest))</td>
<td>[QueryRequest](https://docs.rallydev.com/javarestapi/com/rallydev/rest/request/QueryRequest.html) request*</td>
<td>Query for objects matching the specified request.  By default one page of data will be returned.  Paging will automatically be performed if a limit is set on the request.  Returns a [QueryResponse](https://docs.rallydev.com/javarestapi/com/rallydev/rest/response/QueryResponse.html) object containing the results of the request.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    QueryRequest defectRequest = new QueryRequest("defect");

    defectRequest.setFetch(new Fetch("FormattedID", "Name", "State", "Priority"));
    defectRequest.setQueryFilter(new QueryFilter("State", "=", "Fixed").and(new QueryFilter("Priority", "=", "Resolve Immediately")));
    defectRequest.setOrder("Priority ASC,FormattedID ASC");

    defectRequest.setPageSize(25);
    defectRequest.setLimit(100);

    QueryResponse queryResponse = restApi.query(defectRequest);
</java>
</td>
</tr>

<tr>
<td>[setWsapiVersion](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#setWsapiVersion(java.lang.String))</td>
<td>String wsapiVersion*</td>
<td>Specifies the version of Rally's web services API to use.  Defaults to 1.33</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    restApi.setWsapiVersion("1.29");
</java>
</td>
</tr>

<tr>
<td>[getWsapiVersion](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#getWsapiVersion())</td>
<td> </td>
<td>Gets the version of Rally's web services that the Toolkit is configured to use.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    String version = restApi.getWsapiVersion();
</java>
</td>
</tr>

<tr>
<td>[setApplicationName](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#setApplicationName(java.lang.String))</td>
<td>String name*</td>
<td>Set the name of your application.  This name can be whatever you wish, and is added to the request headers of any web service requests your application makes.  We encourage you to set this value, as it helps track usage of the toolkit.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    restApi.setApplicationName("Universal Rally Data Extractor");
</java>
</td>
</tr>

<tr>
<td>[setApplicationVersion](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#setApplicationVersion(java.lang.String))</td>
<td>String version*</td>
<td>Set the version of the application using the Java Toolkit.  This version can be whatever you wish, and is added to the request headers of any web service requests your application makes.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    restApi.setApplicationVersion("1.1");
</java>
</td>
</tr>

<tr>
<td>[setApplicationVendor](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#setApplicationVendor(java.lang.String)l)</td>
<td>String vendor*</td>
<td>Set the vendor of the application using the Java Toolkit.  This name can be whatever you wish (usually your company name), and is added to the request headers of any web service requests your application makes.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    restApi.setApplicationVendor("My Company, Inc.");
</java>
</td>
</tr>

<tr>
<td>[setProxy](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#setProxy(java.net.URI))</td>
<td>java.net.URI proxy*
String userName
String password</td>
<td>Set the proxy server to use, if you connect to Rally through a proxy server.  By default, no proxy is configured.  The userName and password parameters are optional, and are used if your proxy server requires authentication.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    restApi.setProxy(new URI("http://myproxy.mycompany.com"), "MyProxyUsername", "MyProxyPassword");
</java>
</td>
</tr>

<tr>
<td>[close](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html#close())</td>
<td> </td>
<td>Closes open connections and releases resources.  You should always call this method before your application exits.</td>
</tr>

<tr>
<td colspan=3>**Example:**
<java>
    restApi.close();
</java>
</td>
</tr>

</tbody>
</table>

&nbsp; * = required parameter</div>

## </a>Examples

The following code illustrates how to create, read, update, and delete a defect using the [RallyRestApi](https://docs.rallydev.com/javarestapi/com/rallydev/rest/RallyRestApi.html) object.
```
<java>
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
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
import java.net.URI;
import java.net.URISyntaxException;

public class CrudExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        RallyRestApi restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), "user@company.com", "password");
        restApi.setApplicationName("CrudExample");

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
</java>
```

The following code illustrates how to query for the top 5 highest priority defects:
```
<java>
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class QueryExample {

    public static void main(String[] args) throws URISyntaxException, IOException {

        //Create and configure a new instance of RallyRestApi
        RallyRestApi restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), "user@company.com", "password");
        restApi.setApplicationName("QueryExample");

        try {

            System.out.println("Querying for top 5 highest priority unfixed defects...");

            QueryRequest defects = new QueryRequest("defect");

            defects.setFetch(new Fetch("FormattedID", "Name", "State", "Priority"));
            defects.setQueryFilter(new QueryFilter("State", "<", "Fixed"));
            defects.setOrder("Priority ASC,FormattedID ASC");

            //Return up to 5, 1 per page
            defects.setPageSize(1);
            defects.setLimit(5);

            QueryResponse queryResponse = restApi.query(defects);
            if (queryResponse.wasSuccessful()) {
                System.out.println(String.format("\nTotal results: %d", queryResponse.getTotalResultCount()));
                System.out.println("Top 5:");
                for (JsonElement result : queryResponse.getResults()) {
                    JsonObject defect = result.getAsJsonObject();
                    System.out.println(String.format("\t%s - %s: Priority=%s, State=%s",
                            defect.get("FormattedID").getAsString(),
                            defect.get("Name").getAsString(),
                            defect.get("Priority").getAsString(),
                            defect.get("State").getAsString()));
                }
            } else {
                System.err.println("The following errors occurred: ");
                for (String err : queryResponse.getErrors()) {
                    System.err.println("\t" + err);
                }
            }

        } finally {
            //Release resources
            restApi.close();
        }
    }
}
</java>
```

## Development
### Releasing to Maven central
#### One-time Setup
* Install Maven 3
* Install gpg to sign the artifacts

```bash
brew install gpg
```
* Get a copy of the the gpg key pair archive from Matt Cholick, Kyle Morse, or Charles Ferentchak
* Place the contents of the key pair archive in ~/.gnupg
* Add login login information to Sonatype's repository to your ~/.m2/settings.xml (again getting the credentials from Matt, Kyle, or Charles)

```xml
<settings>
    <servers>
        <server>
            <id>sonatype-nexus-snapshots</id>
            <username>user</username>
            <password>password</password>
        </server>
        <server>
            <id>sonatype-nexus-staging</id>
            <username>user</username>
            <password>password</password>
        </server>
    </servers>
</settings>
```
#### Releasing to Sonatype OSS (central)
This is a stripped down version of a much, much longer guide https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide

* Prepare the release (create tag & update pom)

```bash
mvn release:clean
mvn release:prepare
```
* Release!

```bash
mvn release:perform
```

Finally, draft a Github release from the tag created by Maven.
