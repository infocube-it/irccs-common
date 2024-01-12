package org.quarkus.irccs.client.exception;

import ca.uhn.fhir.parser.DataFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.List;

@Provider
public class DataFormatExceptionMapper extends AbstractExceptionMapper<DataFormatException> {

    public DataFormatExceptionMapper(FhirClient<OperationOutcome> operationOutcomeClient) {
        super(operationOutcomeClient);
    }

    @Override
    public Response toResponse(DataFormatException exception) {
        // Use the method from the base class
        String payload = encodeOperationOutcome(new OperationOutcome().setIssue(List.of(new OperationOutcome.OperationOutcomeIssueComponent().setCode(OperationOutcome.IssueType.STRUCTURE).setSeverity(OperationOutcome.IssueSeverity.ERROR).setDiagnostics(exception.getMessage()))));
        return Response.status(Response.Status.BAD_REQUEST).entity(payload).build();
    }
}