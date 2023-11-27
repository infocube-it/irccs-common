package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.Patient;

public class PatientAssembler {

    public static Patient createEmptyPatient() {
        Patient patient = new Patient();
        patient.setActive(false);
        return patient;
    }

}
