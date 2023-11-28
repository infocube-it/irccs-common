package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.ResearchStudy;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IResearchStudyClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;



public class ResearchStudyClient extends CustomFhirContext  {
    private final int queryLimit;
    private final IGenericClient iGenericClient;

    private final IResearchStudyClient iResearchStudyClient;


    public ResearchStudyClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();

        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iResearchStudyClient = fhirRestClientConfiguration.newRestfulClient(IResearchStudyClient.class);
    }



    public ResearchStudy getResearchStudyById(IIdType theId) {
        return iResearchStudyClient.getResearchStudyById(theId);
    }


    public ResearchStudy updateResearchStudyById(String id, ResearchStudy researchStudy) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_RESEARCHSTUDY, id);
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
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
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
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_RESEARCHSTUDY, id);
        study.setId(idType.toString());
        iGenericClient.update().resource(study).execute();
        return study;
    }

    public OperationOutcome deleteStudyById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_RESEARCHSTUDY, id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }


}
