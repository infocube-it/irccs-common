package org.quarkus.irccs.annotations.models.clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkus.irccs.annotations.models.Group;
import org.quarkus.irccs.annotations.models.User;
import org.quarkus.irccs.common.constants.FhirConst;

@Path("/fhir/Group")
@RegisterRestClient(configKey="group-microservice-client")
@Produces(FhirConst.FHIR_MEDIA_TYPE)
@Consumes(FhirConst.FHIR_MEDIA_TYPE)
public interface GroupControllerClient {
    @POST
    String create(@HeaderParam("Authorization") String jwtToken, @HeaderParam("organizationId") String organizationId, @HeaderParam("isDataManager") boolean isDataManager, @HeaderParam("isOrgAdmin") boolean isOrgAdmin, String payload);

    @DELETE
    @Path("/$bulk_delete")
    String bulkDelete(@HeaderParam("Authorization") String jwtToken, @QueryParam("_filter") String query);

}
