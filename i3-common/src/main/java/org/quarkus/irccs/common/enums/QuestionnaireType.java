package org.quarkus.irccs.common.enums;

public enum QuestionnaireType {
    PROCTC("PROCTC","V1"),
    CRF_GROUP("CRF_GROUP", "V1"),
    QUESTIONNAIRE("QUESTIONNAIRE", "V1"),
    EORTC("EORTC", "V1"),
    CTC_V4("CTC","V4"),
    CTC_V5("CTC", "V5");


    public String system;
    public String value;

   public QuestionnaireType getQuestionnaireBySystem(String system) {
       return QuestionnaireType.valueOf(system);
   }

    QuestionnaireType(String system, String value){
        this.system = system;
        this.value = value;
    }
}

