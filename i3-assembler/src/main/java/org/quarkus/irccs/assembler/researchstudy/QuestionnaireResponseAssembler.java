package org.quarkus.irccs.assembler.researchstudy;


import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.DataType;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.QuestionnaireResponse;
import org.quarkus.irccs.common.constants.FhirConst;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireResponseAssembler {


    public static QuestionnaireResponse createWithHasAnswerAndAnswer() {
        QuestionnaireResponse resource = new QuestionnaireResponse();
        resource.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);

        QuestionnaireResponse.QuestionnaireResponseItemComponent child1 = createItem(new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent());
        resource.addItem(child1);

        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();

        DataType dataAnswer = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE_RESPONSE, resource.getIdPart());
        dataAnswer.setId("q1");
        answer.setValue(dataAnswer);
        child1.addAnswer(answer);

        return  resource;
    }

    private static QuestionnaireResponse.QuestionnaireResponseItemComponent createItem(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answerComponent) {
        List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> listAnswer = new ArrayList<>();
        listAnswer.add(answerComponent);
        QuestionnaireResponse.QuestionnaireResponseItemComponent item = new QuestionnaireResponse.QuestionnaireResponseItemComponent();
        item.setLinkId("id-1");
        item.setAnswer(listAnswer);
        return item;
    }


    public static QuestionnaireResponse createQuestionnaireResponse(String sezione, String id) {
        //Questionnaire Object
        QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();

        if(id != null) {
            IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE, id);
            questionnaireResponse.setId(idType);
        }

        List<QuestionnaireResponse.QuestionnaireResponseItemComponent> listItm = new ArrayList<>();

        //DataType
        QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireItemComponent = new QuestionnaireResponse.QuestionnaireResponseItemComponent();
        questionnaireItemComponent.setLinkId("1");
        questionnaireItemComponent.setText("Data del ritiro");
        //questionnaireItemComponent.setAnswer();

        QuestionnaireResponse.QuestionnaireResponseItemComponent questionnaireItemComponent2 = new QuestionnaireResponse.QuestionnaireResponseItemComponent();
        questionnaireItemComponent2.setLinkId("2");
        questionnaireItemComponent2.setText("Note");
        //questionnaireItemComponent.setAnswer();

        listItm.add(questionnaireItemComponent);
        listItm.add(questionnaireItemComponent2);

        //questionnaireResponse populate
        questionnaireResponse.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
        questionnaireResponse.setItem(listItm);

        return  questionnaireResponse;

    }


}
