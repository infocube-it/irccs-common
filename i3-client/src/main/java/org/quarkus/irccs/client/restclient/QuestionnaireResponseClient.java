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
import org.hl7.fhir.r5.model.QuestionnaireResponse;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IQuestionnaireResponseClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;



public class QuestionnaireResponseClient  extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;

    private final IQuestionnaireResponseClient iQuestionnaireResponseClient;


    public QuestionnaireResponseClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iQuestionnaireResponseClient = fhirRestClientConfiguration.newRestfulClient(IQuestionnaireResponseClient.class);
    }


    public QuestionnaireResponse getQuestionnaireResponseById(IIdType theId) {
        return iQuestionnaireResponseClient.getQuestionnaireResponseById(theId);
    }


    public IIdType createQuestionnaireResponse(QuestionnaireResponse questionnaireResponse) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(questionnaireResponse)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }
    public QuestionnaireResponse updateQuestionnaireResponse(String id, QuestionnaireResponse questionnaireResponse) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE_RESPONSE, id);
        questionnaireResponse.setId(idType.toString());
        iGenericClient.update().resource(questionnaireResponse).execute();
        return questionnaireResponse;
    }

    public Bundle getAllQuestionnairesResponse() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(QuestionnaireResponse.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deleteQuestionnaireResponseById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE_RESPONSE, id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public Bundle searchQuestionnaireResponseByCode(String code) {
        return iGenericClient.search()
                .forResource(QuestionnaireResponse.class)
                .where(QuestionnaireResponse.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }

}
