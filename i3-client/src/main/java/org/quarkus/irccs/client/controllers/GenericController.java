package org.quarkus.irccs.client.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.restclient.FhirClient;
import org.quarkus.irccs.common.constants.FhirConst;

@ApplicationScoped
@Consumes(FhirConst.FHIR_MEDIA_TYPE)
@Produces(FhirConst.FHIR_MEDIA_TYPE)
public abstract class GenericController<T extends IBaseResource>{
    @Inject
    @Singleton
    public FhirClient<T> fhirClient;

    @GET
    public String search(@Context UriInfo searchParameters) {
        return fhirClient.encodeResourceToString(fhirClient.readAll(searchParameters));
    }
    @GET
    @Path("/_search")
    public String searchPath(@Context UriInfo searchParameters) {
        return fhirClient.encodeResourceToString(fhirClient.readAll(searchParameters));
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
        return fhirClient.encodeResourceToString(fhirClient.read(id));
    }

    @POST
    public String create(String payload) {
        return fhirClient.encodeResourceToString(fhirClient.create(payload));
    }

    @PUT
    @Path("/{id}")
    public String update(@PathParam("id") String id, String payload) {
        return fhirClient.encodeResourceToString(fhirClient.update(id, payload));
    }

    @DELETE
    @Path("/{id}")
    public OperationOutcome delete(@PathParam("id") String id) {
        return fhirClient.delete(id);
    }

}
