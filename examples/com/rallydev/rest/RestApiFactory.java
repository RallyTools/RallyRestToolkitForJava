package com.rallydev.rest;

import java.net.URI;
import java.net.URISyntaxException;

public class RestApiFactory {

    //Specify your Rally server
    private static final String SERVER = "https://rally1.rallydev.com";

    //Specify your WSAPI version
    private static final String WSAPI_VERSION = "v2.0";

    //Specify your Rally username
    private static final String USERNAME = "YOUR_USERNAME";

    //Specify your Rally password
    private static final String PASSWORD = "YOUR_PASSWORD";

    //If using a proxy specify full url, like http://my.proxy.com:8000
    private static final String PROXY_SERVER = null;

    //If using an authenticated proxy server specify the username and password
    private static final String PROXY_USERNAME = null;
    private static final String PROXY_PASSWORD = null;

    public static RallyRestApi getRestApi() throws URISyntaxException {
        RallyRestApi restApi = new RallyRestApi(new URI(SERVER), USERNAME, PASSWORD);
        if (PROXY_SERVER != null) {
            URI uri = new URI(PROXY_SERVER);
            if (PROXY_USERNAME != null) {
                restApi.setProxy(uri, PROXY_USERNAME, PROXY_PASSWORD);
            } else {
                restApi.setProxy(uri);
            }
        }

        restApi.setWsapiVersion(WSAPI_VERSION);

        return restApi;
    }
}
