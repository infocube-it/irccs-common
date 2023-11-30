package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.RequestScoped;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IStructureDefinitionClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;



public class StructureDefinitionClient extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IStructureDefinitionClient iStructureDefinitionClient;

    public StructureDefinitionClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iStructureDefinitionClient = fhirRestClientConfiguration.newRestfulClient(IStructureDefinitionClient.class);
    }

    public StructureDefinition getStructureDefinitionById(IIdType theId) {
        return iStructureDefinitionClient.getStructureDefinitionById(theId);
    }


    public IIdType createStructureDefinition(StructureDefinition structureDefinition) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(structureDefinition)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }
    public StructureDefinition updateStructureDefinition(String id, StructureDefinition structureDefinition) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE, id);
        structureDefinition.setId(idType.toString());
        iGenericClient.update().resource(structureDefinition).execute();
        return structureDefinition;
    }

    public Bundle getAllStructureDefinitions() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(StructureDefinition.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deleteStructureDefinitionById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_STRUCTURE_DEFINITION, id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public Bundle searchStructureDefinitionByCode(String code) {
        return iGenericClient.search()
                .forResource(StructureDefinition.class)
                .where(StructureDefinition.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }

}
