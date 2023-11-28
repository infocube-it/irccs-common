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
import org.hl7.fhir.r5.model.Questionnaire;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IQuestionnaireClient;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;



public class QuestionnaireClient extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;

    private final IQuestionnaireClient iQuestionnaireClient;


    public QuestionnaireClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.queryLimit = queryLimit;

        fhirContext.getRestfulClientFactory().setSocketTimeout(30000);

        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);

        iQuestionnaireClient = fhirContext.newRestfulClient(IQuestionnaireClient.class, serverBase);
    }



    public Questionnaire getQuestionnaireById(IIdType theId) {
        return iQuestionnaireClient.getQuestionnaireById(theId);
    }


    public IIdType createQuestionnaire(Questionnaire questionnaire) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(questionnaire)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }
    public Questionnaire updateQuestionnaire(String id, Questionnaire questionnaire) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE, id);
        questionnaire.setId(idType.toString());
        iGenericClient.update().resource(questionnaire).execute();
        return questionnaire;
    }

    public Bundle getAllQuestionnaires() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(Questionnaire.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deleteQuestionnaireById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE, id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public Bundle searchQuestionnaireByCode(String code) {
        return iGenericClient.search()
                .forResource(Questionnaire.class)
                .where(Questionnaire.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }

}
