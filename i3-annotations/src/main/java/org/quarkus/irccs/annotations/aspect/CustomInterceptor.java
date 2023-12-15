package org.quarkus.irccs.annotations.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpServerRequest;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;

import org.quarkus.irccs.annotations.interfaces.I3Role;

import java.io.Serializable;
import java.util.Base64;


@I3Role
@Interceptor
public class CustomInterceptor implements Serializable {

    @Context
    HttpServerRequest request;

    @AroundInvoke
    public Object logResponseAndHeader(InvocationContext ctx) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String tokenKey = "Authorization";

        if(request.getHeader(tokenKey) != null) {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks =  request.getHeader(tokenKey).split("\\.");
            String payload = new String(decoder.decode(chunks[1]));
            System.out.println("Payload is: "+ payload);
        }

        System.out.println("Response is: " + objectMapper.writeValueAsString( ctx.proceed()));
        return ctx.proceed();
    }


}

