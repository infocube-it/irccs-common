package org.quarkus.irccs.client.exception;

import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.restclient.FhirClient;

@Provider
public class BaseServerResponseExceptionMapper extends AbstractExceptionMapper<BaseServerResponseException> {

    public BaseServerResponseExceptionMapper(FhirClient<OperationOutcome> operationOutcomeClient) {
        super(operationOutcomeClient);
    }

    @Override
    public Response toResponse(BaseServerResponseException exception) {
        // Use the method from the base class
        return Response.status(exception.getStatusCode()).entity(encodeOperationOutcome((OperationOutcome) exception.getOperationOutcome())).build();
    }
}
