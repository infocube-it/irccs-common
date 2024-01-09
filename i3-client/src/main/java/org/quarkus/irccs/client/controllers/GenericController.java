package org.quarkus.irccs.client.controllers;

import ca.uhn.fhir.rest.api.DeleteCascadeModeEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.IdType;
import org.quarkus.irccs.client.restclient.FhirClient;
import org.quarkus.irccs.common.constants.FhirConst;

import java.lang.reflect.ParameterizedType;


@ApplicationScoped
@Consumes(FhirConst.FHIR_MEDIA_TYPE)
@Produces(FhirConst.FHIR_MEDIA_TYPE)
public abstract class GenericController<T extends IBaseResource>{
    @Inject
    protected FhirClient<T> fhirClient;

    @GET
    public String search(@Context UriInfo searchParameters) {
        String queryParams = fhirClient.convertToQueryString(searchParameters.getQueryParameters());
        return fhirClient.encodeResourceToString(fhirClient.readAll(queryParams));
    }

    @GET
    @Path( "/{id}")
    public String read(@PathParam("id") String id) {
        return fhirClient.encodeResourceToString(fhirClient.read(new IdType(fhirClient.getResourceType().getSimpleName(), id)));
    }

    @POST
    public String create(String payload) {
        T object = fhirClient.parseResource(fhirClient.getResourceType(), payload);

        IIdType idType = fhirClient.create(object);
        return fhirClient.encodeResourceToString(
                fhirClient.read(idType));
    }

    @PUT
    @Path("/{id}")
    public String update(@PathParam("id") String id, String payload) {
        T object = fhirClient.parseResource(fhirClient.getResourceType(), payload);
        return fhirClient.encodeResourceToString(fhirClient.update(id, object));
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        fhirClient.delete(id);
    }

}
