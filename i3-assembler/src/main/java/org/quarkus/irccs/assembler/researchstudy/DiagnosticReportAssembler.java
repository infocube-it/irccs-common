package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.CarePlan;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.hl7.fhir.r5.model.Reference;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticReportAssembler {

    public static DiagnosticReport createDiagnosticReport(CarePlan carePlan) {
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        List<Reference> carePlainList = new ArrayList<>();
        carePlainList.add(new Reference(carePlan.getId()));
        diagnosticReport.setBasedOn(carePlainList);

        return diagnosticReport;
    }

}
