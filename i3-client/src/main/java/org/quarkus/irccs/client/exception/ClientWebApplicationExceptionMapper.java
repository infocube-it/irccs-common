package org.quarkus.irccs.client.exception;

import ca.uhn.fhir.parser.DataFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.List;

@Provider
public class ClientWebApplicationExceptionMapper extends AbstractExceptionMapper<ClientWebApplicationException> {

    public ClientWebApplicationExceptionMapper(FhirClient<OperationOutcome> operationOutcomeClient) {
        super(operationOutcomeClient);
    }

    @Override
    public Response toResponse(ClientWebApplicationException exception) {
        // Use the method from the base class
        String payload = encodeOperationOutcome(new OperationOutcome().setIssue(List.of(new OperationOutcome.OperationOutcomeIssueComponent().setCode(OperationOutcome.IssueType.STRUCTURE).setSeverity(OperationOutcome.IssueSeverity.ERROR).setDiagnostics(exception.getCause().getMessage()))));
        return Response.status(exception.getResponse().getStatus()).entity(payload).build();
    }
}