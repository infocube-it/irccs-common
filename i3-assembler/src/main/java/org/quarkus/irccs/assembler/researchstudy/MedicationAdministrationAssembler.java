package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.MedicationAdministration;

public class MedicationAdministrationAssembler {


    public static MedicationAdministration createMedicationAdministration(String amministratore) {
        MedicationAdministration medicationAdministration = new MedicationAdministration();
        //medicationAdministration.setStatus(MedicationAdministration.MedicationAdministrationStatus.INPROGRESS);
        return medicationAdministration;
    }

}
