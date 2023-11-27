package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.CarePlan;
import org.hl7.fhir.r5.model.ServiceRequest;
import org.quarkus.irccs.client.interfaces.IServiceRequest;


@ApplicationScoped
public class ServiceRequestClient {

    private final IGenericClient iGenericClient;

    private final IServiceRequest iServiceRequest;


    @Inject
    ServiceRequestClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();

        ctx.getRestfulClientFactory().setSocketTimeout(30000);

        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iServiceRequest = ctx.newRestfulClient(IServiceRequest.class, serverBase);

    }



    public CarePlan getServiceRequestById(IIdType theId){
        return  iServiceRequest.getServiceRequestById(theId);
    }



    public IIdType createServiceRequest(ServiceRequest serviceRequest) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(serviceRequest)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }


}
