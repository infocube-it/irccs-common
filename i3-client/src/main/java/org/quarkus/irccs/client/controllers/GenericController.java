package org.quarkus.irccs.client.controllers;

import ca.uhn.fhir.rest.annotation.Delete;
import jakarta.decorator.Decorator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.quarkus.irccs.client.restclient.FhirClient;
import org.quarkus.irccs.common.constants.FhirConst;

import java.util.List;
import java.util.Map;

@Consumes(FhirConst.FHIR_MEDIA_TYPE)
@Produces(FhirConst.FHIR_MEDIA_TYPE)
public abstract class GenericController<T extends IBaseResource>{
    @Inject
    @Context
    public FhirClient<T> fhirClient;

    @GET
    public String search(@Context UriInfo searchParameters) {
        return search_Internal(searchParameters.getQueryParameters());
    }
    @GET
    @Path("/_search")
    public String searchPath(@Context UriInfo searchParameters) {
        return searchPath_Internal(searchParameters.getQueryParameters());
    }
    @GET
    @Path("/_history")
    public String history() {
        return fhirClient.encodeResourceToString(fhirClient.history());
    }

    @GET
    @Path("{id}/_history")
    public String historyPath(@PathParam("id") String id) {
        return fhirClient.encodeResourceToString(fhirClient.historyPath(id));
    }

    @GET
    @Path("{id}/_history/{version_id}")
    public String historyPathVersion(@PathParam("id") String id, @PathParam("version_id") String versionId) {
        return fhirClient.encodeResourceToString(fhirClient.historyPathVersion(id, versionId));
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

    @GET
    @Path("/{id}/$evaluate")
    public String evaluate(@PathParam("id") String id) {
        return fhirClient.evaluate(id);
    }


    @DELETE
    @Path("/$bulk_delete")
    public String bulkDelete(@Context UriInfo searchParameters) {
        MultivaluedMap<String, String> queryParams = searchParameters.getQueryParameters();

        List<Bundle.BundleEntryComponent> entries = this.fhirClient.readAll(queryParams).getEntry();
        entries.forEach(entry -> {
            String resourceId = entry.getResource().getIdPart();
            this.fhirClient.delete(resourceId);
        });

        return "Ok";
    }

    public String search_Internal(Map<String, List<String>> searchParameters) {
        return fhirClient.encodeResourceToString(fhirClient.readAll(searchParameters));
    }
    public String searchPath_Internal(Map<String, List<String>> searchParameters) {
        return fhirClient.encodeResourceToString(fhirClient.readAll(searchParameters));
    }

    public FhirClient<T> getFhirClient() {
        return fhirClient;
    }
}
