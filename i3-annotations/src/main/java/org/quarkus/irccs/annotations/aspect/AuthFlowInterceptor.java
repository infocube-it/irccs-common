package org.quarkus.irccs.annotations.aspect;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quarkus.irccs.annotations.interfaces.SyncAuthFlow;
import org.quarkus.irccs.annotations.models.AuthMicroserviceClient;
import org.quarkus.irccs.client.controllers.GenericController;

@Interceptor
@SyncAuthFlow
@Priority(0)
public class AuthFlowInterceptor {

    @RestClient
    AuthMicroserviceClient authMicroserviceClient;

    @Inject
    JsonWebToken jwt;

    @AroundInvoke
    public Object modifyPayload(InvocationContext context) throws Exception {
        Object response;

        LookupTable lookupTable = new LookupTable(authMicroserviceClient, jwt);
        response = lookupTable.intercept(((GenericController<?>) context.getTarget()).fhirClient, context);
        return response;
    }
}

