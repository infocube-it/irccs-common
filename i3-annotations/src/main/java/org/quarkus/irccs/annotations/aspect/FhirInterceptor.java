package org.quarkus.irccs.annotations.aspect;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.annotations.models.clients.GroupControllerClient;
import org.quarkus.irccs.client.restclient.FhirClient;

@ApplicationScoped
public class FhirInterceptor {

    private static final Logger logger = Logger.getLogger(FhirInterceptor.class);
    private final AuthMicroserviceClient authClient;
    private final GroupControllerClient groupClient;
    private final JsonWebToken jwt;
    private final HttpHeaders httpHeaders;

    @Inject
    public FhirInterceptor(@RestClient AuthMicroserviceClient authClient, @RestClient GroupControllerClient groupControllerClient, JsonWebToken jwt, HttpHeaders httpHeaders) {
        this.authClient = authClient;
        this.groupClient = groupControllerClient;
        this.jwt = jwt;
        this.httpHeaders = httpHeaders;
    }

    protected String intercept(FhirClient<?> fhirClient, InvocationContext context) throws Exception {
        logger.info("intercept start");
        Flow flow = null;

        String resourceType = fhirClient.getResourceType().getSimpleName().toLowerCase(); // Get the resource type name in lowercase
        logger.info("intercept resourceType:"+resourceType);
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
            
            logger.info("intercept end");
            return (String) context.proceed();
            
        }

       
    }
}