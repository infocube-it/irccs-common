package org.quarkus.irccs.annotations.aspect;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quarkus.irccs.annotations.interfaces.SyncAuthFlow;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.annotations.models.clients.GroupControllerClient;
import org.quarkus.irccs.client.controllers.GenericController;

@Interceptor
@SyncAuthFlow
@Priority(0)
public class AuthFlowInterceptor {

    @RestClient
    AuthMicroserviceClient authMicroserviceClient;
    @RestClient
    GroupControllerClient groupControllerClient;

    @Inject
    JsonWebToken jwt;
    @Inject
    HttpHeaders httpHeaders;


    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        FhirInterceptor interceptor = new FhirInterceptor(authMicroserviceClient, groupControllerClient, jwt, httpHeaders);
        return interceptor.intercept(((GenericController<?>) context.getTarget()).fhirClient, context);
    }
}

