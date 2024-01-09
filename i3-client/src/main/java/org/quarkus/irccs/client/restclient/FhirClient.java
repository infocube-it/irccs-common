package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.rest.api.DeleteCascadeModeEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.param.StringParam;
import jakarta.ws.rs.core.UriBuilder;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirQueryConst;

import java.util.List;
import java.util.Map;


public class FhirClient<T extends IBaseResource> extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final Class<T> resourceType;

    public FhirClient(FhirRestClientConfiguration fhirRestClientConfiguration, Class<T> resourceType) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        this.iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        this.resourceType = resourceType;
    }

    public T getResourceById(IIdType theId) {
        return iGenericClient.read().resource(resourceType).withId(theId).execute();
    }

    public T getResourceById(String theId) {
        IIdType idType = new IdType(resourceType.getSimpleName(), theId);
        return iGenericClient.read().resource(resourceType).withId(idType).execute();
    }

    public IIdType createResource(T resource) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }

    public T updateResource(String id, T resource) {
        IIdType idType = new IdType(resourceType.getSimpleName(), id);
        resource.setId(idType.toString());
        iGenericClient.update().resource(resource).execute();
        return resource;
    }

    public IIdType updateResource(T resource) {
        MethodOutcome outcome = iGenericClient.update()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

    public Bundle getAllResources(String searchParameters) {
        if(!searchParameters.isEmpty()) {
            return searchResources(searchParameters);
        }

        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE, SortOrderEnum.DESC);
        return iGenericClient.search()
                .forResource(resourceType)
                .count(queryLimit)
                .sort(sortSpec)
                .returnBundle(Bundle.class)
                .execute();
    }

    public OperationOutcome deleteResourceById(String id, DeleteCascadeModeEnum cascadeMode) {
        if(cascadeMode.describeConstable().isEmpty()){
            cascadeMode = DeleteCascadeModeEnum.NONE;
        }

        MethodOutcome response = iGenericClient.delete()
                .resourceById(new IdType(resourceType.getSimpleName(), id))
                .cascade(cascadeMode)
                .execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public Bundle getResourceByCode(TokenClientParam identifier, String code){
        return returnBundle(query().where(identifier.exactly().code(code)));
    }

    public IQuery<?> query() {
        return iGenericClient.search()
                .forResource(resourceType);
    }

    public Bundle returnBundle(IQuery<?> query){
        return query
                .returnBundle(Bundle.class)
                .execute();
    }

    public Bundle searchResources(String searchParameters) {
        // Execute the search and return the result
        return iGenericClient.fetchResourceFromUrl(Bundle.class, resourceType.getSimpleName() + "?" + searchParameters);
    }

    public Class<T> getResourceType() {
        return resourceType;
    }
}
