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



    public static ResearchStudy createResearchStudy(Practitioner practitioner, PlanDefinition planDefinition, List<Group> groups) {
        // Study params
        String nomeStudio                           = "MITO40";
        String eudract                              = "2020-001386-37";
        String description                          = "Studio osservazionale, retrospettivo, multicentrico, no profit, dal titolo 'Studio osservazionale retrospettivo, multicentrico, mirato a raccogliere gli outcomes clinici del “Niraparib” come terapia di mantenimento in pazienti affetti da tumore ovarico epiteliale di alto grado, delle tube di falloppio o peritoneale primitivo dopo risposta (parziale o completa) alla chemioterapia di prima linea a base di platino: an observational real-life study: MITO 40'.";

        int maxPazienti                             = 50;
        String fase                                 = "phase-1";
        String primoCommento                        = "Referente studio Dr.Pignata +393336363666";
        String secondoCommento                      = "Lo studio avrà inizio tra due mesi";
        Enumerations.PublicationStatus stato        = Enumerations.PublicationStatus.ACTIVE;

        //Research Object
        List<Reference> protocols = new ArrayList<>();
        ResearchStudy researchStudy = new ResearchStudy();
        List<Identifier> identifiers = new ArrayList<>();
        List<Annotation> annotations = new ArrayList<>();
        List<ResearchStudy.ResearchStudyComparisonGroupComponent> comparisonGroupComponents = new ArrayList<>();
        List<ResearchStudy.ResearchStudyAssociatedPartyComponent> associatedPartyComponents = new ArrayList<>();


        for(Group group : groups) {
            ResearchStudy.ResearchStudyComparisonGroupComponent researchStudyComparisonGroupComponent = new ResearchStudy.ResearchStudyComparisonGroupComponent();
            researchStudyComparisonGroupComponent.setObservedGroup(new Reference(group));

            comparisonGroupComponents.add(researchStudyComparisonGroupComponent);
        }

        researchStudy.setComparisonGroup(comparisonGroupComponents);

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
        Annotation annotation = new Annotation(primoCommento);
        Annotation secondAnnotation = new Annotation(secondoCommento);
        annotations.add(annotation);
        annotations.add(secondAnnotation);

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
