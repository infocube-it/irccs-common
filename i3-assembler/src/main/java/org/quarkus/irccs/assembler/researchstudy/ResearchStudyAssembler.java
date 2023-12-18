package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.*;

import java.util.ArrayList;
import java.util.List;

public class ResearchStudyAssembler {


    public static ResearchStudy.ResearchStudyAssociatedPartyComponent getAmministratoriForResearchStudy(String amministratore, Coding coding) {
        CodeableConcept codeableConcept = new CodeableConcept(coding);
        ResearchStudy.ResearchStudyAssociatedPartyComponent researchStudyAssociatedPartyComponent = new ResearchStudy.ResearchStudyAssociatedPartyComponent();
        researchStudyAssociatedPartyComponent.setName(amministratore);
        researchStudyAssociatedPartyComponent.setRole(codeableConcept);

        return researchStudyAssociatedPartyComponent;
    }


    public static ResearchStudy addDiagnosticReportToResearchStudy(ResearchStudy researchStudy, DiagnosticReport diagnosticReport) {
        List<Reference> theResult = researchStudy.getResult();
        theResult.add(new Reference(diagnosticReport.getId()));
        researchStudy.setResult(theResult);
        return researchStudy;
    }

    public static ResearchStudy.ResearchStudyRecruitmentComponent createResearchStudyRecruitmentComponent(Group group) {
        ResearchStudy.ResearchStudyRecruitmentComponent researchStudyRecruitmentComponent = new ResearchStudy.ResearchStudyRecruitmentComponent();
        researchStudyRecruitmentComponent.setActualGroup(new Reference(group.getId()));
        return  researchStudyRecruitmentComponent;
    }



    public static ResearchStudy createResearchStudy(Practitioner practitioner, PlanDefinition planDefinition) {
        // Study params
        String nomeStudio                           = "Nome studio";
        String eudract                              = "Eudract camp";
        String description                          = "La mia descrizione di studio";

        int maxPazienti                             = 5;
        String fase                                 = "phase-1";
        String commento                             = "Commento di default";
        Enumerations.PublicationStatus stato        = Enumerations.PublicationStatus.ACTIVE;

        //Research Object
        List<Reference> protocols = new ArrayList<>();
        ResearchStudy researchStudy = new ResearchStudy();
        List<Identifier> identifiers = new ArrayList<>();
        List<Annotation> annotations = new ArrayList<>();
        List<ResearchStudy.ResearchStudyAssociatedPartyComponent> associatedPartyComponents = new ArrayList<>();


        //Add Nome
        researchStudy.setTitle(nomeStudio);
        researchStudy.setDescription(description);

        //Add Referent
        if(practitioner != null) {
            ResearchStudy.ResearchStudyAssociatedPartyComponent researchStudyAssociatedPartyComponent = new ResearchStudy.ResearchStudyAssociatedPartyComponent();
            researchStudyAssociatedPartyComponent.setParty(new Reference(practitioner.getId()));
            associatedPartyComponents.add(researchStudyAssociatedPartyComponent);
            researchStudy.setAssociatedParty(associatedPartyComponents);
        }


        //Add PlanDefinition
        if(planDefinition != null) {
            protocols.add(new Reference(planDefinition));
            researchStudy.setProtocol(protocols);
        }

        //Add Eudract
        Identifier identifier = new Identifier();
        identifier.setValue(eudract);
        identifiers.add(identifier);
        researchStudy.setIdentifier(identifiers);

        //Setto lo stato
        researchStudy.setStatus(stato);

        //Aggiunta commento
        Annotation annotation = new Annotation(commento);
        annotations.add(annotation);
        researchStudy.setNote(annotations);

        //Aggiunta MaxPaxienti
        researchStudy.getRecruitment().setTargetNumber(maxPazienti);

        //Aggiunta Fase
        CodeableConcept phase = new CodeableConcept();
        phase.setText(fase);
        researchStudy.setPhase(phase);


        return  researchStudy;
    }


}
