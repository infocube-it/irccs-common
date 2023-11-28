package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IDiagnosticReport;



public class DiagnosticReportClient  extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IDiagnosticReport iDiagnosticReport;


    public DiagnosticReportClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.queryLimit = queryLimit;

        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);

        iDiagnosticReport = fhirContext.newRestfulClient(IDiagnosticReport.class, serverBase);

    }

    public DiagnosticReport getDiagnosticReport(IIdType iIdType) {
        return iDiagnosticReport.getDiagnosticReportById(iIdType);
    }

    public IIdType createDiagnosticReport(DiagnosticReport diagnosticReport) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(diagnosticReport)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

}
