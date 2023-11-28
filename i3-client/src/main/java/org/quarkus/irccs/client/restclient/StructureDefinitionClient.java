package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;


public class StructureDefinitionClient extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;

    public StructureDefinitionClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();

        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
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
