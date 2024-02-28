package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.ws.rs.core.UriInfo;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;


public class FhirClient<T extends IBaseResource> extends CustomFhirContext {
    private final IGenericClient iGenericClient;
    private final Class<T> resourceType;

    private final FhirContext fhirContext;

    public FhirClient(FhirRestClientConfiguration fhirRestClientConfiguration, Class<T> resourceType) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        this.fhirContext = fhirRestClientConfiguration.getFhirContext();
        this.resourceType = resourceType;
    }

    public T read(IIdType theId) {
        return iGenericClient.read().resource(resourceType).withId(theId).execute();
    }

    public T read(String theId) {
        IIdType idType = new IdType(resourceType.getSimpleName(), theId);
        return iGenericClient.read().resource(resourceType).withId(idType).execute();
    }

    public T create(String payloadResource) {
        T resource = parseResource(getResourceType(), payloadResource);
        MethodOutcome outcome = iGenericClient.create()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();
        return this.read(outcome.getId());
    }

    public T update(String id, String payloadResource) {
        T resource = parseResource(getResourceType(), payloadResource);
        IIdType idType = new IdType(resourceType.getSimpleName(), id);
        resource.setId(idType.toString());
        iGenericClient.update().resource(resource).execute();
        return resource;
    }

    public Bundle readAll(UriInfo parameters) {
        return iGenericClient.search()
                .forResource(resourceType)
                .totalMode(SearchTotalModeEnum.ACCURATE) //ritorna sempre il campo total nella ricerca
                .whereMap(parameters.getQueryParameters())
                .returnBundle(Bundle.class)
                .execute();
    }

    public OperationOutcome delete(String id) {
        MethodOutcome response = iGenericClient.delete()
                .resourceById(new IdType(resourceType.getSimpleName(), id))
                .execute();
        return (OperationOutcome) response.getOperationOutcome();
    }

    public Class<T> getResourceType() {
        return resourceType;
    }

    public Bundle history() {
        return iGenericClient.history().onType(resourceType).returnBundle(Bundle.class).execute();
    }

    public Bundle historyPath(String theId) {
        IIdType idType = new IdType(resourceType.getSimpleName(), theId);
        return (iGenericClient.history().onInstance(idType)).returnBundle(Bundle.class).execute();
    }

    public T historyPathVersion(String theId, String versionId) {
        return iGenericClient.read().resource(resourceType).withIdAndVersion(theId, versionId).execute();
    }
}
