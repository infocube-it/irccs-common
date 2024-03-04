package org.quarkus.irccs.authentication.model;

import com.fasterxml.jackson.annotation.JsonInclude;
public class ResponseBody {

    public int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String body;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String token;
}

