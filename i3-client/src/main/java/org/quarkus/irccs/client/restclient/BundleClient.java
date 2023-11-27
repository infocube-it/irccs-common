package org.quarkus.irccs.client.restclient;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Organization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BundleClient {
    private final IGenericClient iGenericClient;

    @Inject
    BundleClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();

        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);
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