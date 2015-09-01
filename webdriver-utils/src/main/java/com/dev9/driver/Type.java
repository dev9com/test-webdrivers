package com.dev9.driver;

/**
 * User: yurodivuie
 * Date: 6/2/13
 * Time: 4:32 PM
 */
public enum Type {

    LOCAL, REMOTE;

    public static Type fromJson(String text) {
        return valueOf(text.toUpperCase());
    }
}
