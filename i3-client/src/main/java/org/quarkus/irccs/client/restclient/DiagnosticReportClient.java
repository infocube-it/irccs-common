package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.DiagnosticReport;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IDiagnosticReport;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;


public class DiagnosticReportClient extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IDiagnosticReport iDiagnosticReport;


    public DiagnosticReportClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();

        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iDiagnosticReport = fhirRestClientConfiguration.newRestfulClient(IDiagnosticReport.class);

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

    public OperationOutcome deleteDiagnosticReportById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_DIAGNOSTIC_REPORT, id)).execute();
        return (OperationOutcome) response.getOperationOutcome();
    }

}
