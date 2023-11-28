package org.quarkus.irccs.client.restclient;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.inject.Inject;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Organization;
import org.quarkus.irccs.client.context.CustomFhirContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BundleClient extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;

    public BundleClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.queryLimit = queryLimit;

        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);
    }

    public void createOrganizations(List<Organization> organizations) {
        Bundle bundle = new Bundle(Bundle.BundleType.COLLECTION);
        List<Bundle.BundleEntryComponent> bundleEntryComponentList = new ArrayList<>();

        for (Organization organization : organizations){
            Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
            bundleEntryComponent.setResource(organization);
            bundleEntryComponentList.add(bundleEntryComponent);
        }

        bundle.setId(UUID.randomUUID().toString());
        bundle.setEntry(bundleEntryComponentList);
        MethodOutcome methodOutcome = iGenericClient.create()
                .resource(bundle)
                .prettyPrint()
                .encodedJson()
                .execute();

        methodOutcome.getId().getIdPart();
    }


    public OperationOutcome deleteBundleById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType("Bundle", id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }


    public List<Bundle.BundleEntryComponent> getAllBundle() {
        return iGenericClient
                .search()
                .forResource(Bundle.class)
                .returnBundle(Bundle.class)
                .execute().getEntry();
    }

}