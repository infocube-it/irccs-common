package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.common.constants.FhirConst;


import java.util.ArrayList;
import java.util.List;

public class ServiceRequestAssembler {

    public static PlanDefinition createPlanDefinition(CarePlan carePlan) {
        PlanDefinition planDefinition = new PlanDefinition();
        Enumeration<Enumerations.PublicationStatus> enumerations
                = new Enumeration<Enumerations.PublicationStatus>(new Enumerations.PublicationStatusEnumFactory());
        List<PlanDefinition.PlanDefinitionActionComponent> planDefinitionActionComponentList = new ArrayList<>();
        PlanDefinition.PlanDefinitionActionComponent planDefinitionActionComponent = new PlanDefinition.PlanDefinitionActionComponent();

        planDefinitionActionComponent.setLinkId(FhirConst.RESOURCE_TYPE_CAREPLAN +"/"+ carePlan.getIdPart());
        //planDefinitionActionComponent.setLinkId(carePlan.getId());

        planDefinitionActionComponentList.add(planDefinitionActionComponent);
        enumerations.setValue(Enumerations.PublicationStatus.ACTIVE);
        planDefinition.setStatusElement(enumerations);
        planDefinition.setAction(planDefinitionActionComponentList);
        return planDefinition;
    }



    public static PlanDefinition createPlanDefinition(String id, Questionnaire questionnaire) {
        PlanDefinition planDefinition = new PlanDefinition();
        Enumeration<Enumerations.PublicationStatus> enumerations
                = new Enumeration<Enumerations.PublicationStatus>(new Enumerations.PublicationStatusEnumFactory());

        if(id != null) {
            IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_PLAN_DEFINITION, id);
            planDefinition.setId(idType);
        }

        List<PlanDefinition.PlanDefinitionActionComponent> planDefinitionActionComponentList = new ArrayList<>();
        PlanDefinition.PlanDefinitionActionComponent planDefinitionActionComponent = new PlanDefinition.PlanDefinitionActionComponent();

        //Reference reference = new Reference();
        //reference.setReference(questionnaire.getId());
        //reference.setId(questionnaire.getId());
        //reference.setType(questionnaire.getResourceType().toString());
        //reference.setReferenceElement(questionnaire.getIdElement());

        DataType idType = new IdType(FhirConst.RESOURCE_TYPE_QUESTIONNAIRE, questionnaire.getIdPart());

        planDefinitionActionComponent.setDefinition(idType);

        planDefinitionActionComponentList.add(planDefinitionActionComponent);
        enumerations.setValue(Enumerations.PublicationStatus.ACTIVE);
        planDefinition.setStatusElement(enumerations);
        planDefinition.setAction(planDefinitionActionComponentList);

        return planDefinition;
    }


    public static PlanDefinition createPlanDefinition(String id, List<ContactDetail> contactDetail) {
        PlanDefinition planDefinition = new PlanDefinition();

        if(id != null) {
            IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_PLAN_DEFINITION, id);
            planDefinition.setId(idType);
        }

        planDefinition.setContact(contactDetail);


        return planDefinition;
    }

}
