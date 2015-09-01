package com.dev9.conf;

import java.net.MalformedURLException;
import java.net.URL;
import com.typesafe.config.Config;


/**
 * User: yurodivuie
 * Date: 5/27/13
 * Time: 6:07 PM
 */
public class SauceLabsCredentials {

    private static final Config CONFIG = WebtestConfigFactory.getConfig();
    private static final String SAUCELABS_USER_PATH = "saucelabs.user";
    private static final String SAUCELABS_KEY_PATH = "saucelabs.key";
    private static final String SAUCELABS_SERVER_PATH = "saucelabs.server";

    public static String getUser() {
        return safelyGetConfigString(CONFIG, SAUCELABS_USER_PATH);
    }

    public static String getKey() {
        return safelyGetConfigString(CONFIG, SAUCELABS_KEY_PATH);
    }

    public static String getServer() {
        return safelyGetConfigString(CONFIG, SAUCELABS_SERVER_PATH);
    }

    public static URL getConnectionLocation() {
        URL url;
        try {
            String urlString = String.format("http://%s:%s@%s", getUser(), getKey(), getServer());
            url = new URL( urlString );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to parse remote selenium server connection information", e);
        }
        return url;
    }

    private static String safelyGetConfigString(Config config, String path) {
        if (!config.hasPath(path) || config.getString(path).isEmpty()) {
            String message = String.format("Config value %s missing - required for Sauce Labs connection.", path);
            throw new RuntimeException(message);
        }
        return config.getString(path);
    }
}
