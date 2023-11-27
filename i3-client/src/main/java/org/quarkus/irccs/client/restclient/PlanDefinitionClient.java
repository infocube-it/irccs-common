package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.PlanDefinition;
import org.quarkus.irccs.client.interfaces.IPlanDefinitionClient;
import org.quarkus.irccs.common.constants.FhirConst;


@ApplicationScoped
public class PlanDefinitionClient {
    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;
    private final IGenericClient iGenericClient;

    private final IPlanDefinitionClient iPlanDefinitionClient;

    @Inject
    PlanDefinitionClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();


        ctx.getRestfulClientFactory().setSocketTimeout(30000);

        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iPlanDefinitionClient = ctx.newRestfulClient(IPlanDefinitionClient.class, serverBase);
    }



    public PlanDefinition getPlanDefinitionById(IIdType theId) {
        return iPlanDefinitionClient.getPlanDefinitionById(theId);
    }

    public IIdType createPlanDefinition(PlanDefinition planDefinition) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(planDefinition)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }
    public PlanDefinition updatePlanDefinition(String id, PlanDefinition planDefinition) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_PLAN_DEFINITION, id);
        planDefinition.setId(idType.toString());
        iGenericClient.update().resource(planDefinition).execute();
        return planDefinition;
    }

    public Bundle getAllPlanDefinitions() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
                SortOrderEnum.DESC);
        return  iGenericClient.search()
                        .forResource(PlanDefinition.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deletePlanDefinitionById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType("PlanDefinition", id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }


    public Bundle searchPlanDefinitionByCode(String code) {
        return iGenericClient.search()
                .forResource(PlanDefinition.class)
                .where(PlanDefinition.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }
}
