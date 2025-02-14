package org.quarkus.irccs.annotations.models.clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkus.irccs.annotations.models.Group;
import org.quarkus.irccs.annotations.models.User;

@Path("/fhir/auth")
@RegisterRestClient(configKey="auth-microservice-client")
public interface AuthMicroserviceClient {

    @Path("/users/create")
    @POST
    Response createUser(@HeaderParam("Authorization") String jwtToken, User user);
    @Path("/users")
    @PUT
    Response updateUser(@HeaderParam("Authorization") String jwtToken, User user);
    @Path("/users")
    @DELETE
    Response deleteUser(@HeaderParam("Authorization") String jwtToken, String id);
    @Path("/groups")
    @PUT
    Response updateGroup(@HeaderParam("Authorization") String jwtToken, Group group);

    @Path("/groups")
    @POST
    Response createGroup(@HeaderParam("Authorization") String jwtToken, Group group);

    @Path("/groups")
    @DELETE
    Response deleteGroup(@HeaderParam("Authorization") String jwtToken, String id);
}
