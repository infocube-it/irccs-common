package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.Group;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Questionnaire;
import org.hl7.fhir.r5.model.Resource;
import org.quarkus.irccs.common.enums.QuestionnaireType;

import java.util.ArrayList;
import java.util.List;

public class GroupAssembler {

    public static Group createAGroup(String name, int maxPax) {
        Group group = new Group();
        group.setName(name);
        group.setQuantity(maxPax);
        return group;
    }

    public static Group createAGroup(String name, int maxPax, Questionnaire questionnaire){
        Group group = createAGroup(name,maxPax);
        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(questionnaire);
        group.setContained(resourceList);

        return group;
    }



    public static Group createGroupOfResearchStudy(Questionnaire  groupQuestionnaire, String nome) {
        Group group = new Group();
        group.setName(nome);
        group.setType(Group.GroupType.PERSON);
        group.setDescription("Gruppo di persone " + nome);
        group.setMembership(Group.GroupMembershipBasis.DEFINITIONAL);

        //Add Questionnare Group
        List<Resource> contained = new ArrayList<>();
        contained.add(groupQuestionnaire);

        group.setContained(contained);
        return group;
    }

}