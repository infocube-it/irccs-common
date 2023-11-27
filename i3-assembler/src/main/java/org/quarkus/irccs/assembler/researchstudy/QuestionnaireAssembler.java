package org.quarkus.irccs.assembler.researchstudy;


import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.common.enums.QuestionnaireType;
import org.quarkus.irccs.common.constants.FhirConst;


import java.util.ArrayList;
import java.util.List;
public class QuestionnaireAssembler {

    public static Questionnaire createCTC(QuestionnaireType questionnaireType, String medDRACode, String cTCAETerm, String grade) {

        //todo:: QuestionnaireItemAnswerOptionComponent have a DataType

        Questionnaire questionnaire = new Questionnaire();

        List<Questionnaire.QuestionnaireItemAnswerOptionComponent> answerOption = new ArrayList<>();
        List<Questionnaire.QuestionnaireItemComponent> items = new ArrayList<>();
        List<Coding> codes =getCode(medDRACode, cTCAETerm);

        Questionnaire.QuestionnaireItemAnswerOptionComponent questionnaireItemAnswerOptionComponent = new Questionnaire.QuestionnaireItemAnswerOptionComponent();

        questionnaireItemAnswerOptionComponent.setValue(new StringType(grade));

        answerOption.add(questionnaireItemAnswerOptionComponent);

        Questionnaire.QuestionnaireItemComponent questionnaireItemComponent = new Questionnaire.QuestionnaireItemComponent();
        //questionnaireItemComponent.setText(medDRASOC);

        items.add(questionnaireItemComponent);



        questionnaireItemComponent.setAnswerOption(answerOption);

        questionnaireItemComponent.setType(Questionnaire.QuestionnaireItemType.CODING);
        questionnaireItemComponent.setCode(codes);

        questionnaire.setStatus(Enumerations.PublicationStatus.UNKNOWN);
        questionnaire.setIdentifier(getIdentifier(questionnaireType));
        questionnaire.setItem(items);

        return questionnaire;


    }

    public static Questionnaire createEORTC(QuestionnaireType questionnaireType, String answ) {


        //todo:: QuestionnaireItemAnswerOptionComponent have a DataType

        Questionnaire questionnaire = new Questionnaire();

        List<Questionnaire.QuestionnaireItemAnswerOptionComponent> answerOption = new ArrayList<>();
        List<Questionnaire.QuestionnaireItemComponent> items = new ArrayList<>();

        Questionnaire.QuestionnaireItemAnswerOptionComponent questionnaireItemAnswerOptionComponent = new Questionnaire.QuestionnaireItemAnswerOptionComponent();

        questionnaireItemAnswerOptionComponent.setValue(new StringType(answ));

        answerOption.add(questionnaireItemAnswerOptionComponent);

        Questionnaire.QuestionnaireItemComponent questionnaireItemComponent = new Questionnaire.QuestionnaireItemComponent();

        items.add(questionnaireItemComponent);

        questionnaireItemComponent.setAnswerOption(answerOption);
        questionnaire.setStatus(Enumerations.PublicationStatus.UNKNOWN);
        questionnaire.setItem(items);
        questionnaire.setIdentifier(getIdentifier(questionnaireType));



        return questionnaire;


    }

    public static Questionnaire createProCTC(QuestionnaireType questionnaireType, String answ) {


        //todo:: QuestionnaireItemAnswerOptionComponent have a DataType

        Questionnaire questionnaire = new Questionnaire();

        List<Questionnaire.QuestionnaireItemAnswerOptionComponent> answerOption = new ArrayList<>();
        List<Questionnaire.QuestionnaireItemComponent> items = new ArrayList<>();

        Questionnaire.QuestionnaireItemAnswerOptionComponent questionnaireItemAnswerOptionComponent = new Questionnaire.QuestionnaireItemAnswerOptionComponent();

        questionnaireItemAnswerOptionComponent.setValue(new StringType(answ));

        answerOption.add(questionnaireItemAnswerOptionComponent);

        Questionnaire.QuestionnaireItemComponent questionnaireItemComponent = new Questionnaire.QuestionnaireItemComponent();

        items.add(questionnaireItemComponent);

        questionnaireItemComponent.setAnswerOption(answerOption);
        questionnaire.setStatus(Enumerations.PublicationStatus.UNKNOWN);
        questionnaire.setItem(items);
        questionnaire.setIdentifier(getIdentifier(questionnaireType));



        return questionnaire;


    }

    private static List<Coding> getCode(String medDRACode, String cTCAETerm) {
        List<Coding> codes = new ArrayList<>();

        //todo:: refatoring with the CodingCTCTerm enum
        Coding meddra = new Coding();
        meddra.setCode(medDRACode);
        meddra.setDisplay("medDRACode");
        meddra.setSystem("https://www.meddra.org");
        codes.add(meddra);

        Coding ctcae = new Coding();
        ctcae.setDisplay("cTCAETerm");
        ctcae.setSystem("https://ncit.nci.nih.gov/");
        ctcae.setCode(cTCAETerm);
        codes.add(ctcae);

        return codes;
    }

    public static List<Identifier> getIdentifier(QuestionnaireType questionnaireType){
        Identifier identifier = new Identifier();
        identifier.setSystem(questionnaireType.system);
        identifier.setValue(questionnaireType.value);
        identifier.setUse(Identifier.IdentifierUse.SECONDARY);
        return new ArrayList<>(List.of(identifier));
    }


    public static Questionnaire createWithHasAnswerAndAnswer() {
        Questionnaire resource = new Questionnaire();
        resource.setStatus(Enumerations.PublicationStatus.ACTIVE);

        Questionnaire.QuestionnaireItemComponent child1 = createItem(Questionnaire.QuestionnaireItemType.GROUP);
        resource.addItem(child1);

        Questionnaire.QuestionnaireItemEnableWhenComponent enableWhen = new Questionnaire.QuestionnaireItemEnableWhenComponent();
        enableWhen.setQuestion("q1");
        enableWhen.setAnswer(new StringType("a value"));
//		enableWhen.setHasAnswer(true);
        child1.addEnableWhen(enableWhen);

        Questionnaire.QuestionnaireItemComponent child21 = createItem(Questionnaire.QuestionnaireItemType.STRING);
        child1.addItem(child21);



        return  resource;
    }

    private static Questionnaire.QuestionnaireItemComponent createItem(Questionnaire.QuestionnaireItemType type) {
        Questionnaire.QuestionnaireItemComponent item = new Questionnaire.QuestionnaireItemComponent();
        item.setLinkId("id-1");
        item.setType(type);
        return item;
    }




    public static Questionnaire createEmptyQuestionnaire() {
        //Questionnaire Object
        //todo:: Add identifier
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIdentifier(getIdentifier(QuestionnaireType.QUESTIONNAIRE));
        questionnaire.setStatus(Enumerations.PublicationStatus.UNKNOWN);
        return questionnaire;
    }

    public static Questionnaire createQuestionnaire(String sezione, String id) {
        //Questionnaire Object
        Questionnaire questionnaire = new Questionnaire();
        List<Questionnaire.QuestionnaireItemComponent> item = new ArrayList<>();


        if(id != null) {
            IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE, id);
            questionnaire.setId(idType);
        }



        questionnaire.setName("Nome");
        questionnaire.setDescription("Tempo Campi/Controllo");


        List<Questionnaire.QuestionnaireItemComponent> listItm = new ArrayList<>();

        //Sezioni
        Questionnaire.QuestionnaireItemComponent questionnaireItemComponent = new Questionnaire.QuestionnaireItemComponent();
        questionnaireItemComponent.setText("Cardiovascolare");
        questionnaireItemComponent.setType(Questionnaire.QuestionnaireItemType.STRING);

        Questionnaire.QuestionnaireItemComponent questionnaireItemComponent2 = new Questionnaire.QuestionnaireItemComponent();
        questionnaireItemComponent2.setText("Pomonare");
        questionnaireItemComponent2.setType(Questionnaire.QuestionnaireItemType.STRING);

        listItm.add(questionnaireItemComponent);
        listItm.add(questionnaireItemComponent2);

        //questionnaire populate
        questionnaire.setStatus(Enumerations.PublicationStatus.ACTIVE);
        questionnaire.setTitle(sezione);
        questionnaire.addSubjectType("Patient");
        questionnaire.setItem(listItm);

        return  questionnaire;

    }


}
