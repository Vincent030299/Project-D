package com.example.swisscom;

import java.lang.reflect.GenericArrayType;
import java.util.List;

public class Post {

    private String lang;
    private String query;
    private String sessionId;
    private String timezone;

    public Result result;

    public Post(String lang, String query, String sessionId, String timezone) {
        this.lang = lang;
        this.query = query;
        this.sessionId = sessionId;
        this.timezone = timezone;
    }

    public Result getResult() {
        return result;
    }
}
