package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.impl.GenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import jakarta.enterprise.context.ApplicationScoped;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;


public class FhirResourceClient<T extends IBaseResource> extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final Class<T> resourceType;

    public FhirResourceClient(FhirRestClientConfiguration fhirRestClientConfiguration, Class<T> resourceType) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        this.iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        this.resourceType = resourceType;
    }

    public T getResourceById(IIdType theId) {
        return iGenericClient.read().resource(resourceType).withId(theId).execute();
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

    public Bundle getAllResources() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE, SortOrderEnum.DESC);
        return iGenericClient.search()
                .forResource(resourceType)
                .count(queryLimit)
                .sort(sortSpec)
                .returnBundle(Bundle.class)
                .execute();
    }

    public OperationOutcome deleteResourceById(String id) {
        MethodOutcome response = iGenericClient.delete()
                .resourceById(new IdType(resourceType.getSimpleName(), id))
                .execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

   public Bundle getResourceByCode(String code) {
       StringClientParam param = new StringClientParam(BaseResource.SP_RES_ID);

        return iGenericClient.search()
                .forResource(resourceType)
                .where(param.matches().value(code))
                        .returnBundle(Bundle.class)
                        .execute();
    }


}
