package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.quarkus.irccs.client.interfaces.IDiagnosticReport;


@ApplicationScoped
public class DiagnosticReportClient {

    private final IGenericClient iGenericClient;
    private IDiagnosticReport iDiagnosticReport;

    @Inject
    DiagnosticReportClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iDiagnosticReport = ctx.newRestfulClient(IDiagnosticReport.class, serverBase);

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
