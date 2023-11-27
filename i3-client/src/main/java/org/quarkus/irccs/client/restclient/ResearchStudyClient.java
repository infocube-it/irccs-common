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
import org.hl7.fhir.r5.model.ResearchStudy;
import org.quarkus.irccs.client.interfaces.IResearchStudyClient;
import org.quarkus.irccs.common.constants.FhirConst;


@ApplicationScoped
public class ResearchStudyClient {

    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;
    private final IGenericClient iGenericClient;

    private final IResearchStudyClient iResearchStudyClient;

    @Inject
    ResearchStudyClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        ctx.getRestfulClientFactory().setSocketTimeout(30000);

        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iResearchStudyClient = ctx.newRestfulClient(IResearchStudyClient.class, serverBase);
    }



    public ResearchStudy getResearchStudyById(IIdType theId) {
        return iResearchStudyClient.getResearchStudyById(theId);
    }


    public ResearchStudy updateResearchStudyById(String id, ResearchStudy researchStudy) {
        IIdType idType = new IdType("Organization", id);
        researchStudy.setId(idType.toString());
        iGenericClient.update().resource(researchStudy).execute();
        return researchStudy;
    }


    public IIdType createResearchStudy(ResearchStudy researchStudy) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(researchStudy)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

    public Bundle searchStudyByCode(String code) {
        return iGenericClient.search()
                .forResource(ResearchStudy.class)
                .where(ResearchStudy.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }

    public Bundle getAllStudies() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(ResearchStudy.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public ResearchStudy updateStudy(String id, ResearchStudy study) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE, id);
        study.setId(idType.toString());
        iGenericClient.update().resource(study).execute();
        return study;
    }

    public OperationOutcome deleteStudyById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType("ResearchStudy", id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }


}
