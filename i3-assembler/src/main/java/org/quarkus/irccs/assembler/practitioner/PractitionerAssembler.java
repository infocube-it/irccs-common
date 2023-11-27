package org.quarkus.irccs.assembler.practitioner;

import org.hl7.fhir.r5.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PractitionerAssembler {


    public static Practitioner createPractitioner() {
        Practitioner practitioner = new Practitioner();

        ArrayList<HumanName> names = new ArrayList<>();
        names.add(createHumanName("Dr.", "Carla", "Carlucci"));
        practitioner.setName(names);
        practitioner.setGender(Enumerations.AdministrativeGender.OTHER);
        practitioner.setBirthDate(new Date());
        practitioner.setDeceased(new BooleanType(false));
        practitioner.setTelecom(createContactPoint("3387678787", "0826768987", "CarlaCarlucci@Pascal.it"));


        return practitioner;
    }

    public static HumanName createHumanName(String prefix, String firstName, String lastName) {
        HumanName humanName = new HumanName();
        if(null != prefix){
            List<StringType> prefixes = new ArrayList<>();
            prefixes.add(new StringType(prefix));
            humanName.setPrefix(prefixes);
        }

        List<StringType> firstNames = new ArrayList<>();
        firstNames.add(new StringType(firstName));


        humanName.setGiven(firstNames);
        humanName.setFamily(lastName);
        humanName.setText(prefix + " " + firstName + " " + lastName);
        humanName.setUse(HumanName.NameUse.USUAL);

        return humanName;
    }

    public static List<ContactPoint> createContactPoint(String phone, String fax, String email) {
        ContactPoint contactPointPhone = new ContactPoint();
        contactPointPhone.setSystem(ContactPoint.ContactPointSystem.PHONE);
        contactPointPhone.setValue(phone);

        ContactPoint contactPointEmail  = new ContactPoint();
        contactPointEmail.setSystem(ContactPoint.ContactPointSystem.EMAIL);
        contactPointEmail.setValue(email);

        ContactPoint contactPointFax  = new ContactPoint();
        contactPointFax.setSystem(ContactPoint.ContactPointSystem.FAX);
        contactPointFax.setValue(fax);

        List<ContactPoint> contactPoints = new ArrayList<>();
        contactPoints.add(contactPointPhone);
        contactPoints.add(contactPointFax);
        contactPoints.add(contactPointEmail);

        return contactPoints;

    }
}
