package org.quarkus.irccs.common.fhir.converters;

import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.convertors.context.ConversionContext40_50;
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50;
import org.hl7.fhir.convertors.conv40_50.datatypes40_50.general40_50.CodeableConcept40_50;
import org.hl7.fhir.convertors.conv40_50.datatypes40_50.general40_50.Identifier40_50;
import org.hl7.fhir.convertors.conv40_50.datatypes40_50.special40_50.Meta40_50;
import org.hl7.fhir.convertors.conv40_50.datatypes40_50.special40_50.Reference40_50;
import org.hl7.fhir.r5.model.*;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

public class FHIRResearchStudyConverter extends VersionConvertor_40_50 {

    public FHIRResearchStudyConverter(@Nonnull BaseAdvisor_40_50 advisor) {
        super(advisor);
    }

    public ResearchStudy convertR4ToR5(org.hl7.fhir.r4.model.ResearchStudy studyR4) {
        ConversionContext40_50.INSTANCE.init(this, studyR4.fhirType());
        ResearchStudy studyR5 = new ResearchStudy();
        studyR5.setId(studyR4.getId());
        studyR5.setMeta(Meta40_50.convertMeta(studyR4.getMeta()));
        studyR5.setIdentifier(studyR4.getIdentifier().stream().map(Identifier40_50::convertIdentifier).collect(Collectors.toList()));
        studyR5.setTitle(studyR4.getTitle());
        studyR5.setProtocol(studyR4.getProtocol().stream().map(Reference40_50::convertReference).collect(Collectors.toList()));
        studyR5.setPartOf(studyR4.getPartOf().stream().map(Reference40_50::convertReference).collect(Collectors.toList()));
        studyR5.setStatus(convertStatusR4toR5(studyR4.getStatus()));
        studyR5.setPrimaryPurposeType(CodeableConcept40_50.convertCodeableConcept(studyR4.getPrimaryPurposeType()));
        studyR5.setPhase(CodeableConcept40_50.convertCodeableConcept(studyR4.getPhase()));
        studyR5.setStudyDesign(studyR4.getCategory().stream().map(CodeableConcept40_50::convertCodeableConcept).collect(Collectors.toList()));
        studyR5.setFocus(studyR4.getFocus().stream().map(CodeableConcept40_50::convertCodeableConceptToCodeableReference).collect(Collectors.toList()));
        studyR5.setRegion(studyR4.getLocation().stream().map(CodeableConcept40_50::convertCodeableConcept).collect(Collectors.toList()));
        studyR5.setDescription(studyR4.getDescription());

        if (studyR4.hasEnrollment()) {
            ResearchStudy.ResearchStudyRecruitmentComponent recruitment = new ResearchStudy.ResearchStudyRecruitmentComponent();
            recruitment.setActualGroup(Reference40_50.convertReference(studyR4.getEnrollmentFirstRep()));
            studyR5.setRecruitment(recruitment);
        }

        studyR5.setExtension(studyR4.getExtension().stream().map(this::convertExtensionR4toR5).collect(Collectors.toList()));

        if (studyR4.hasPrincipalInvestigator()) {
            ResearchStudy.ResearchStudyAssociatedPartyComponent associatedParty = new ResearchStudy.ResearchStudyAssociatedPartyComponent();
            associatedParty.setParty(Reference40_50.convertReference(studyR4.getPrincipalInvestigator()));
            studyR5.addAssociatedParty(associatedParty);
        }
        return studyR5;
    }

    public org.hl7.fhir.r4.model.ResearchStudy convertR5ToR4(ResearchStudy studyR5) {
        ConversionContext40_50.INSTANCE.init(this, studyR5.fhirType());
        org.hl7.fhir.r4.model.ResearchStudy studyR4 = new org.hl7.fhir.r4.model.ResearchStudy();
        studyR4.setMeta(Meta40_50.convertMeta(studyR5.getMeta()));
        studyR4.setId(studyR5.getId());
        studyR4.setIdentifier(studyR5.getIdentifier().stream().map(Identifier40_50::convertIdentifier).collect(Collectors.toList()));
        studyR4.setTitle(studyR5.getTitle());
        studyR4.setProtocol(studyR5.getProtocol().stream().map(Reference40_50::convertReference).collect(Collectors.toList()));
        studyR4.setPartOf(studyR5.getPartOf().stream().map(Reference40_50::convertReference).collect(Collectors.toList()));
        studyR4.setStatus(convertStatusR5toR4(studyR5.getStatus()));
        studyR4.setPrimaryPurposeType(CodeableConcept40_50.convertCodeableConcept(studyR5.getPrimaryPurposeType()));
        studyR4.setPhase(CodeableConcept40_50.convertCodeableConcept(studyR5.getPhase()));
        studyR4.setCategory(studyR5.getStudyDesign().stream().map(CodeableConcept40_50::convertCodeableConcept).collect(Collectors.toList()));
        studyR4.setFocus(studyR5.getFocus().stream().map(CodeableConcept40_50::convertCodeableReferenceToCodeableConcept).collect(Collectors.toList()));
        studyR4.setLocation(studyR5.getRegion().stream().map(CodeableConcept40_50::convertCodeableConcept).collect(Collectors.toList()));
        studyR4.setDescription(studyR5.getDescription());

        if (studyR5.hasRecruitment() && studyR5.getRecruitment().hasActualGroup()) {
            studyR4.addEnrollment(Reference40_50.convertReference(studyR5.getRecruitment().getActualGroup()));
        }

        studyR4.setExtension(studyR5.getExtension().stream().map(this::convertExtensionR5toR4).collect(Collectors.toList()));

        if (!studyR5.getAssociatedParty().isEmpty()) {
            studyR4.setPrincipalInvestigator(Reference40_50.convertReference(studyR5.getAssociatedPartyFirstRep().getParty()));
        }
        return studyR4;
    }

    private static Enumerations.PublicationStatus convertStatusR4toR5(org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus statusR4) {
        if (statusR4 == null) return null;
        return switch (statusR4) {
            case ACTIVE -> Enumerations.PublicationStatus.ACTIVE;
            case WITHDRAWN -> Enumerations.PublicationStatus.RETIRED;
            default -> Enumerations.PublicationStatus.UNKNOWN;
        };
    }

    private static org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus convertStatusR5toR4(Enumerations.PublicationStatus statusR5) {
        if (statusR5 == null) return null;
        return switch (statusR5) {
            case ACTIVE -> org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus.ACTIVE;
            case RETIRED -> org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus.WITHDRAWN;
            default -> org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus.NULL;
        };
    }

    // Metodo per convertire un'estensione da R5 a R4
    private org.hl7.fhir.r4.model.Extension convertExtensionR5toR4(Extension r5Ext) {
        org.hl7.fhir.r4.model.Extension r4Ext = new org.hl7.fhir.r4.model.Extension();
        r4Ext.setUrl(r5Ext.getUrl());
        r4Ext.setValue(r5Ext.getValue());
        return r4Ext;
    }

    // Metodo per convertire un'estensione da R4 a R5
    public Extension convertExtensionR4toR5(org.hl7.fhir.r4.model.Extension r4Ext) {
        Extension r5Ext = new Extension();
        r5Ext.setUrl(r4Ext.getUrl());
        r5Ext.setValue(r4Ext.getValue());
        return r5Ext;
    }
}
