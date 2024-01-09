package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.DeleteCascadeModeEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirQueryConst;

import java.util.List;


public class FhirClient<T extends IBaseResource> extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final Class<T> resourceType;

    private final FhirContext fhirContext;

    public FhirClient(FhirRestClientConfiguration fhirRestClientConfiguration, Class<T> resourceType) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
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

    public IIdType create(T resource) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }

    public T update(String id, T resource) {
        IIdType idType = new IdType(resourceType.getSimpleName(), id);
        resource.setId(idType.toString());
        iGenericClient.update().resource(resource).execute();
        return resource;
    }

    public IIdType update(T resource) {
        MethodOutcome outcome = iGenericClient.update()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

    public Bundle readAll(String searchParameters) {
        if(!searchParameters.isEmpty()) {
            return search(searchParameters);
        }

        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE, SortOrderEnum.DESC);
        return iGenericClient.search()
                .forResource(resourceType)
                .count(queryLimit)
                .sort(sortSpec)
                .returnBundle(Bundle.class)
                .execute();
    }

    public OperationOutcome delete(String id) {

        MethodOutcome response = iGenericClient.delete()
                .resourceById(new IdType(resourceType.getSimpleName(), id))
                .execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public List<T> searchList(String searchParameters) {
        Bundle object = iGenericClient.fetchResourceFromUrl(Bundle.class, resourceType.getSimpleName() + "?" + searchParameters);
        return BundleUtil.toListOfResourcesOfType(fhirContext, object, resourceType);
    }
    
    public Bundle search(String searchParameters) {
        // Execute the search and return the result
        return iGenericClient.fetchResourceFromUrl(Bundle.class, resourceType.getSimpleName() + "?" + searchParameters);
    }

    public Class<T> getResourceType() {
        return resourceType;
    }


}
