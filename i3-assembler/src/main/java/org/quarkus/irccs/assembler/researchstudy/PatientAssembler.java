package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import java.util.ArrayList;
import java.util.List;

public class PatientAssembler {

    public static Patient createEmptyPatient() {
        Patient patient = new Patient();
        patient.setActive(false);
        return patient;
    }

    public static Patient createPatientOfCarePlan() {
        Patient patient = new Patient();
        List<Address> addressList = new ArrayList<>();
        List<StringType> stringTypeList = new ArrayList<>();
        List<ContactPoint> contactPoints =  new ArrayList<>();
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setSystem(ContactPoint.ContactPointSystem.PHONE);
        contactPoints.add(contactPoint);
        patient.setGender(Enumerations.AdministrativeGender.UNKNOWN);
        patient.setTelecom(contactPoints);

        Address address = new Address();
        stringTypeList.add(new StringType(""));
        address.setLine(stringTypeList);
        patient.setAddress(addressList);

        Narrative narrative = new Narrative();

        XhtmlNode xhtmlNode = new XhtmlNode();
        xhtmlNode.div();
        xhtmlNode.table("hapiPropertyTable");

        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        narrative.setDiv(xhtmlNode);
        patient.setText(narrative);
        return patient;
    }

}