package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.CarePlan;
import org.hl7.fhir.r5.model.ServiceRequest;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IServiceRequest;



public class ServiceRequestClient extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;

    private final IServiceRequest iServiceRequest;



    public ServiceRequestClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.queryLimit = queryLimit;
        fhirContext.getRestfulClientFactory().setSocketTimeout(30000);
        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);
        iServiceRequest = fhirContext.newRestfulClient(IServiceRequest.class, serverBase);
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
