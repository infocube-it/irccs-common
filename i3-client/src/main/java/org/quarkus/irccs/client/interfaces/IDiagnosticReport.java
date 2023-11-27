package org.quarkus.irccs.client.interfaces;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.DiagnosticReport;


public interface IDiagnosticReport extends IBasicClient {

    @Read()
    DiagnosticReport getDiagnosticReportById(@IdParam IIdType theId);

}
