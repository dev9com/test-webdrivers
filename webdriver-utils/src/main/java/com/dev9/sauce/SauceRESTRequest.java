package com.dev9.sauce;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Request object used to package requests sent through SauceREST
 *
 * User: yurodivuie
 * Date: 2/29/12
 * Time: 11:05 AM
 */
@Data
@AllArgsConstructor
public class SauceRESTRequest {
    private URL requestUrl;
    private String method;
    private String jsonParameters;
}
