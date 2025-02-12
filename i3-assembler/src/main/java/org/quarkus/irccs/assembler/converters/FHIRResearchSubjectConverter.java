package org.quarkus.irccs.assembler.converters;

/*
public class FHIRResearchSubjectConverter extends VersionConvertor_40_50 {

    public FHIRResearchSubjectConverter(@Nonnull BaseAdvisor_40_50 advisor) {
        super(advisor);
    }

    public ResearchSubject convertR4ToR5(org.hl7.fhir.r4.model.ResearchSubject subjectR4) {
        ConversionContext40_50.INSTANCE.init(this, subjectR4.fhirType());
        ResearchSubject subjectR5 = new ResearchSubject();

        subjectR5.setId(subjectR4.getId());
        subjectR5.setMeta(Meta40_50.convertMeta(subjectR4.getMeta()));
        subjectR5.setIdentifier(subjectR4.getIdentifier().stream().map(Identifier40_50::convertIdentifier).collect(Collectors.toList()));
        //subjectR5.setStatus(convertStatusR4toR5(subjectR4.getStatus())); questo è da gestire con una extension se lo vogliamo
        subjectR5.setProgress(convertStatusR4toR5(subjectR4.getStatus()));
        subjectR5.setStudy(Reference40_50.convertReference(subjectR4.getStudy()));
        subjectR5.setSubject(Reference40_50.convertReference(subjectR4.getIndividual()));
        subjectR5.setConsent(convertConsetR4toR5(subjectR4.getConsentTarget()));
        subjectR5.setActualComparisonGroup();
        subjectR5.setAssignedComparisonGroup()
        return subjectR5;
    }

    private List<Reference> convertConsetR4toR5(org.hl7.fhir.r4.model.Consent consent) {
        List<Reference> ref = new ArrayList<>();
        ref.add(Reference40_50.convertReference(Consent40_50.convertConsent(consent)));
        return ref;
    }

    public org.hl7.fhir.r4.model.ResearchSubject convertR5ToR4(ResearchSubject subjectR5) {
        ConversionContext40_50.INSTANCE.init(this, subjectR5.fhirType());
        org.hl7.fhir.r4.model.ResearchSubject subjectR4 = new org.hl7.fhir.r4.model.ResearchSubject();

        subjectR4.setId(subjectR5.getId());
        subjectR4.setMeta(Meta40_50.convertMeta(subjectR5.getMeta()));
        subjectR4.setIdentifier(subjectR5.getIdentifier().stream().map(Identifier40_50::convertIdentifier).collect(Collectors.toList()));
        subjectR4.setStatus(convertStatusR5toR4(subjectR5.getStatus()));
        subjectR4.setStudy(Reference40_50.convertReference(subjectR5.getStudy()));
        subjectR4.setIndividual(Reference40_50.convertReference(subjectR5.getIndividual()));

        return subjectR4;
    }

    private static ArrayList<ResearchSubject.ResearchSubjectProgressComponent> convertStatusR4toR5(org.hl7.fhir.r4.model.ResearchSubject.ResearchSubjectStatus statusR4) {
        ResearchSubject.ResearchSubjectProgressComponent researchSubjectProgressComponent = new ResearchSubject.ResearchSubjectProgressComponent();
        ArrayList<ResearchSubject.ResearchSubjectProgressComponent> listResearchSub = new ArrayList<ResearchSubject.ResearchSubjectProgressComponent>();
        CodeableConcept codeableConcept = new CodeableConcept();
        List<Coding> listCoding = new ArrayList<>();
        listCoding.add((new Coding("",statusR4.getDefinition(),""));)
        codeableConcept.setCoding(listCoding);
        researchSubjectProgressComponent.setSubjectState(CodeableConcept40_50.convertCodeableConcept(codeableConcept);
        listResearchSub.add(researchSubjectProgressComponent);
        return listResearchSub;
    }

    private static org.hl7.fhir.r4.model.ResearchSubject.ResearchSubjectStatus convertStatusR5toR4(ResearchSubject.ResearchSubjectStatus statusR5) {
        if (statusR5 == null) return org.hl7.fhir.r4.model.ResearchSubject.ResearchSubjectStatus.NULL;
        return org.hl7.fhir.r4.model.ResearchSubject.ResearchSubjectStatus.fromCode(statusR5.toCode());
    }
}
*/