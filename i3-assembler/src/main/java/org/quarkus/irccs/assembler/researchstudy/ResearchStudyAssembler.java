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


    public static ResearchStudy createResearchStudy() {
        // Study params
        String NomeStudio                           = "Nome studio";
        String CommentoItaliano                     = "Commento in italiano";
        String CommentoInglese                      = "Commento in inglese";
        String Eudract                              = "Eudract camp";
        String Referente                            = "Nome referente";
        String Telefono                             = "3336363698";
        String Fax                                  = "000000";
        String NotificaArruolamentoEmail            = "Notifica Arruolamento Email";
        String eMailReferente                       = "mailReferente@provider.xxx";
        String eMailAdesioni                        = "emailAdesioni@provider.xxx";
        String eMailFarmcovig                       = "emailFarmcovif@provider.xxx";
        int MaxPazienti                             = 5;
        int MaxCentri                               = 5;
        String MessaggioSuHomePage                  = "Messaggio su home page";
        String PuoEssereSeguitoDa                   = "può essere seguito da";
        String SegueDa                              = "segue da";
        String VersioneToxCtc                       = "versione toc ctc";
        String versioneProcCTC                      = "version proc ctc";
        String MessaggioSuSceltaGruppo              = "messaggio su scelta gruppo";
        boolean OnLine                              = true;
        //ResearchStudy.ResearchStudyStatus Mostra    = ResearchStudy.ResearchStudyStatus.ACTIVE;
        boolean TestSoloAmmin                       = true;


        //Research Object
        ResearchStudy researchStudy = new ResearchStudy();

        //DataType
        //List<ContactDetail> contactDetailList       = new ArrayList<>();
        //ContactDetail contactDetail                 = new ContactDetail();
        List<Annotation> annotationList             = new ArrayList<>();
        //List<ContactPoint> contactPointList         = new ArrayList<>();

        Annotation commentoItaliano                 = new Annotation();
        Annotation commentoInglese                  = new Annotation();
        Annotation notificaArruolamento             = new Annotation();


        //contactPointList.add(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.FAX).setValue(Fax));
        //contactPointList.add(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(Telefono));
        //contactPointList.add(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.OTHER).setValue(eMailReferente));
        //contactPointList.add(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.OTHER).setValue(eMailAdesioni));
        //contactPointList.add(new ContactPoint().setSystem(ContactPoint.ContactPointSystem.OTHER).setValue(eMailFarmcovig));


        //contactDetail.setTelecom(contactPointList);
        //contactDetailList.add(contactDetail);
        annotationList.add(commentoInglese);
        annotationList.add(commentoItaliano);
        annotationList.add(notificaArruolamento);


        //researchStudy populate
        //researchStudy.setStatus(Mostra);
        researchStudy.setTitle(NomeStudio);
        researchStudy.setNote(annotationList);


        //researchStudy.setContact(contactDetailList);

        return  researchStudy;
    }


}
