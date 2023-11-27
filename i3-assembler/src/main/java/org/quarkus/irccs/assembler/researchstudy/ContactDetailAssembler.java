package org.quarkus.irccs.assembler.researchstudy;


import org.hl7.fhir.r5.model.ContactDetail;
import org.hl7.fhir.r5.model.ContactPoint;

import java.util.ArrayList;
import java.util.List;


public class ContactDetailAssembler {

    //ContactDetail Mock Assembler
    public static ContactDetail createContactDetail() {
        ContactDetail contactDetail = new ContactDetail();

        List<ContactPoint> contactPoints = new ArrayList<>();

        contactPoints.add(createContactPoint(ContactPoint.ContactPointSystem.EMAIL, "info@fhir.org"));
        contactPoints.add(createContactPoint(ContactPoint.ContactPointSystem.FAX, "+0918273645"));
        contactDetail.setName("Admin");
        contactDetail.setTelecom(contactPoints);

        return contactDetail;
    }

    //Utility Method
    private static ContactPoint createContactPoint(ContactPoint.ContactPointSystem type, String value) {

        ContactPoint contactPoint = new ContactPoint();

        contactPoint.setSystem(type);
        contactPoint.setValue(value);

        return contactPoint;

    }


}
