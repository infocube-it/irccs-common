package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.StructureDefinition;

@ApplicationScoped
public class StructureDefinitionClient {

    private final IGenericClient iGenericClient;


    @Inject
    StructureDefinitionClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        ctx.getRestfulClientFactory().setSocketTimeout(30000);
        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);
    }

    public IIdType createStructureDefinition(StructureDefinition structureDefinition) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(structureDefinition)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

}
