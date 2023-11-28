package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.quarkus.irccs.client.context.CustomFhirContext;


public class StructureDefinitionClient extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;

    public StructureDefinitionClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        super(fhirContext);
        this.queryLimit = queryLimit;
        fhirContext.getRestfulClientFactory().setSocketTimeout(30000);
        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);
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
