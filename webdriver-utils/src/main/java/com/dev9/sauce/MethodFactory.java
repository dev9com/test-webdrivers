package com.dev9.sauce;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import lombok.extern.log4j.Log4j2;


/**
 * User: yurodivuie
 * Date: 3/8/12
 * Time: 3:35 PM
 * <p/>
 * Created to isolate more of the HTTPMethod logic.
 */
class MethodFactory {

    private MethodFactory() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class should not be constructed");
    }

    static HttpUriRequest getRequest(String methodType, String url, String optionalJson) {

        HttpUriRequest request;

        if (methodType.equalsIgnoreCase("get")) {
            request = new HttpGet(url);
        } else if (methodType.equalsIgnoreCase("delete")) {
            request = new HttpDelete(url);
        } else {

            HttpEntityEnclosingRequestBase requestBase = null;

            if (methodType.equalsIgnoreCase("post")) {
                requestBase = new HttpPost(url);
            } else if (methodType.equalsIgnoreCase("put")) {
                requestBase = new HttpPut(url);
            }

            if (requestBase != null && optionalJson != null) {
                requestBase.setEntity(new StringEntity(optionalJson, ContentType.APPLICATION_JSON));
            }

            request = requestBase;
        }

        return request;
    }
}
