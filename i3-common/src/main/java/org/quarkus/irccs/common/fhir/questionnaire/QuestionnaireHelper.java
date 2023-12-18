package org.quarkus.irccs.common.fhir.questionnaire;

import org.hl7.fhir.r5.model.Questionnaire;

import java.util.Objects;
import java.util.Optional;

public class QuestionnaireHelper {
    public static Questionnaire adjustLinkId(Questionnaire questionnaire) {
        // last one linkId
        int latestParentId = 1;

        // Flush all QuestionnaireLinkId
        questionnaire.getItem().forEach(questionnaireItemComponent -> {
            questionnaireItemComponent.setLinkId(null);
        });


        // I'll take the last one linkId if exists
        Optional<Questionnaire.QuestionnaireItemComponent> latestQuestionnaireItemComponent = questionnaire.getItem().stream().max((a, b) -> {
            try{
                if(!Objects.equals(a.getLinkId(), b.getLinkId())){
                    return  Integer.compare(Integer.parseInt(a.getLinkId()), Integer.parseInt(b.getLinkId()));
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return -1;
        });

        if(latestQuestionnaireItemComponent.isPresent() && latestQuestionnaireItemComponent.get().getLinkId() != null) {
            latestParentId = Integer.parseInt(latestQuestionnaireItemComponent.get().getLinkId());
        }

        // set parent linkId if not exists
        for (int i = 0; i < questionnaire.getItem().size(); i++) {
            if(questionnaire.getItem().get(i).getLinkId() == null) {
                questionnaire.getItem().get(i).setLinkId(String.valueOf(latestParentId));
                latestParentId++;
            }
            int currentParentId =  Integer.parseInt(questionnaire.getItem().get(i).getLinkId());
            for(int i2 = 0; i2 < questionnaire.getItem().get(i).getItem().size(); i2++) {
                questionnaire.getItem().get(i).getItem().get(i2).setLinkId(currentParentId + "." + i2 );
            }
        }

        return questionnaire;
    }
}
