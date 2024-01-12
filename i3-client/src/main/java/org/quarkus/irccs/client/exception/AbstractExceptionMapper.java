package org.quarkus.irccs.client.exception;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.restclient.FhirClient;

public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
    protected final FhirClient<OperationOutcome> operationOutcomeClient;

    public AbstractExceptionMapper() {
        this.operationOutcomeClient = null;
    }

    public AbstractExceptionMapper(FhirClient<OperationOutcome> operationOutcomeClient) {
        this.operationOutcomeClient = operationOutcomeClient;
    }

    protected String encodeOperationOutcome(OperationOutcome operationOutcome) {
        assert operationOutcomeClient != null;
        return operationOutcomeClient.encodeResourceToString(operationOutcome);
    }
}