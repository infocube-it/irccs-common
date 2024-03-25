package org.quarkus.irccs.annotations.models;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

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
    Response deleteUser(@HeaderParam("Authorization") String jwtToken, @PathParam("id") String id);
    @Path("/groups")
    @PUT
    Response updateGroup(@HeaderParam("Authorization") String jwtToken, Group group);

    @Path("/groups")
    @POST
    Response createGroup(@HeaderParam("Authorization") String jwtToken, Group group);

    @Path("/fhir/auth/users")
    @GET
    Response getAllUsers(@HeaderParam("Authorization") String jwtToken, @QueryParam("email") @DefaultValue("") String email);

    @Path("/fhir/auth/groups")
    @GET
    Response getAllGroups(@HeaderParam("Authorization") String jwtToken, @QueryParam("name") @DefaultValue("") String name);
}
