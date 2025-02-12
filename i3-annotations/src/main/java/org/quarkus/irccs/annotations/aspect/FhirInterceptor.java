package org.quarkus.irccs.annotations.aspect;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hl7.fhir.convertors.conv40_50.resources40_50.Practitioner40_50;
import org.hl7.fhir.r4.model.Practitioner;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.annotations.models.clients.GroupControllerClient;
import org.quarkus.irccs.client.restclient.FhirClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;

@ApplicationScoped
@SuppressWarnings("unchecked")
public class FhirInterceptor {

    private final static Logger LOG = LoggerFactory.getLogger(FhirInterceptor.class);

    private final AuthMicroserviceClient authClient;
    private final GroupControllerClient groupClient;
    private final JsonWebToken jwt;
    private final Map<String, BiFunction<FhirClient<?>, InvocationContext, Object>> lookupTable = new HashMap<>();
    private final HttpHeaders httpHeaders;

    @Inject
    public FhirInterceptor(@RestClient AuthMicroserviceClient authClient, @RestClient GroupControllerClient groupControllerClient, JsonWebToken jwt, HttpHeaders httpHeaders) {
        this.authClient = authClient;
        this.groupClient = groupControllerClient;
        this.jwt = jwt;
        this.httpHeaders = httpHeaders;
    }


    protected String intercept(FhirClient<?> fhirClient, InvocationContext context) throws Exception {
        Flow flow = null;

        String resourceType = fhirClient.getResourceType().getSimpleName().toLowerCase(); // Get the resource type name in lowercase

        switch (resourceType) {
            case "practitioner":
                flow = new PractitionerFlow(fhirClient, context, authClient, jwt, httpHeaders);
                return flow.apply();
            case "group":
                flow = new GroupFlow(fhirClient, context, authClient, jwt, httpHeaders);
                return flow.apply();
            case "organization":
                flow = new OrganizationFlow(fhirClient, context, authClient, jwt, httpHeaders, groupClient);
                return flow.apply();
            default:
                flow = new Flow(fhirClient, context, authClient, jwt, httpHeaders);
                flow.apply();
            return (String) context.proceed();
        }


    }
}