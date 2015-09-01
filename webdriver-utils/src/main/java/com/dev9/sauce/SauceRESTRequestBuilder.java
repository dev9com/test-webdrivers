package com.dev9.sauce;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONValue;

import lombok.extern.log4j.Log4j2;


/**
 * Builder for SauceRESTRequest.  Can be used to construct the URL incrementally.  Provides
 * default values and serialization of maps to JSON strings.
 *
 * User: yurodivuie
 * Date: 2/29/12
 * Time: 11:06 AM
 *
 */
@Log4j2
public class SauceRESTRequestBuilder {
    public static final String REST_URL = "https://saucelabs.com/rest";
    public static final String DEFAULT_VERSION = "v1";

    private String suffix = "";
    private String method = "GET";  //default value;
    private String version = DEFAULT_VERSION;
    private Map<String, Object> jsonMap = new HashMap<String, Object>();

    /**
     * Adds an entry to the object map that will be serialized into JSON string in the
     * request.
     *
     * @param key   String key for the json object.
     * @param value Value can be discrete or a list, map.
     */
    public SauceRESTRequestBuilder addJSON(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    public SauceRESTRequestBuilder setHTTPMethod(String method) {
        this.method = method;
        return this;
    }


    /**
     * Adds standard "/users" section to constructed url.  Does not check location in path.
     */
    public SauceRESTRequestBuilder addUsersToPath() {
        this.suffix += "/users";
        return this;
    }

    /**
     * Adds a user name to the path.  Does not check location in path.
     * @param user  A Sauce Labs user name.
     */
    public SauceRESTRequestBuilder addUserIdToPath(String user) {
        this.suffix += "/";
        this.suffix += user;
        return this;
    }

    /**
     * Adds standard "/jobs" section to constructed url.  Does not check location in path.
     */
    public SauceRESTRequestBuilder addJobsToPath() {
        this.suffix += "/jobs";
        return this;
    }

    /**
     * Adds a job id to the path.  Does not check location in path.
     */
    public SauceRESTRequestBuilder addJobIdToPath(String jobId) {
        this.suffix += "/";
        this.suffix += jobId;
        return this;
    }

    /**
     * Adds a string to the path.  Does not check location.  May require a leading slash.
     * @param suffix    Any string to add to the constructed path.
     */
    public SauceRESTRequestBuilder addGenericSuffix(String suffix) {
        this.suffix += suffix;
        return this;
    }

    /**
     * Sets the version ("v1" is the default).
     * @param version   The version of the REST api in the path.
     */
    public SauceRESTRequestBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Constructs the SauceRESTRequest object from combination of defaults and overrides.
     */
    public SauceRESTRequest build() {

        SauceRESTRequest request = null;
        String json = null;

        if (!jsonMap.isEmpty()) {
            json = JSONValue.toJSONString(jsonMap);
        }

        String stringUrl = REST_URL;

        if (version != null) {
            stringUrl += String.format("/%s", version);
        }

        if (suffix != null) {
            stringUrl += suffix;
        }

        log.trace("Constructed url is {}", stringUrl);

        try {
            URL url = new URL(stringUrl);
            request = new SauceRESTRequest(url, method, json);
        } catch (MalformedURLException e) {
            log.error("Unable to create sauce rest url {}", e);
        }

        return request;
    }

}
