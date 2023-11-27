package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.Group;

public class GroupAssembler {

    public static Group createAGroup(String name, int maxPax) {
        Group group = new Group();
        group.setName(name);
        group.setQuantity(maxPax);

        //group.setManagingEntity(new Reference(carePlan.getId()));
        //group.addMember(new Group.GroupMemberComponent(new Reference(carePlan.getId())));

        return group;

    }
}

