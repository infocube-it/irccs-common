package org.quarkus.irccs.annotations.aspect;

import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hl7.fhir.r5.model.*;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.annotations.models.Group;
import org.quarkus.irccs.annotations.models.clients.GroupControllerClient;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.List;

public class OrganizationFlow extends Flow {

    GroupControllerClient groupClient;

    public OrganizationFlow(FhirClient<?> fhirClient, InvocationContext context, AuthMicroserviceClient authClient, JsonWebToken jwt, HttpHeaders httpHeaders, GroupControllerClient groupClient) {
        super(fhirClient, context, authClient, jwt, httpHeaders);
        this.groupClient = groupClient;
    }


    @Override
    public String create() throws Exception {
        // This will contain the Keycloak Group.
        Group createdOrganizationGroup;
        // We get the Organization response, parse it, and save it in a variable:
        Organization organization = (Organization) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.proceed());
        // We create a Group from the organization data
        Group organizationGroup = Group.groupFromOrganization(organization);
        // We then try to create a keycloak group of the organization;
        try{
            createdOrganizationGroup = authClient.createGroup("Bearer " + jwt.getRawToken(), organizationGroup).readEntity(Group.class);
            /* The keycloak group has now been created, we'll add the ID to the FHIR Organization */
            // An organization can have only one Organization Group. The Id will be saved in the "Identifier" Fhir property as "SECONDARY" (The FIRST one being the actual FHIR ID).
            organization.setIdentifier(List.of(new Identifier().setUse(Identifier.IdentifierUse.SECONDARY).setValue(createdOrganizationGroup.getId())));
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR Organization resource
            fhirClient.delete(organization.getIdPart());
            throw e;
        }

        try {
            // We now send our payload to add the keycloak ID.
            ((FhirClient<Organization>) fhirClient).update(organization);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR Organization resource and the keycloak Group.
            fhirClient.delete(organization.getIdPart());
            authClient.deleteGroup("Bearer " + jwt.getRawToken(), createdOrganizationGroup.getId());
            throw e;
        }

        // We now create the fhir and keycloak groups for the organization management. (Data Manager)
        groupClient.create("Bearer " + jwt.getRawToken(), createdOrganizationGroup.getId(), true, true, getGroupPayload(organization.getName() + " Data Managers"));
        // We now create the fhir and keycloak groups for the organization management. (Admin)
        groupClient.create("Bearer " + jwt.getRawToken(), createdOrganizationGroup.getId(), false, true, getGroupPayload(organization.getName() + " Admins"));

        // If we're successful we return the organization payload.
        return fhirClient.encodeResourceToString(organization);
    }
    @Override
    public String update() throws Exception {
        // We get the payload, parse it, and save it in a response variable:
        Organization organization = (Organization) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.getParameters()[1]);
        // We add the id manually
        organization.setId((String) context.getParameters()[0]);
        // We create a Group from the organization data
        Group organizationGroup = Group.groupFromOrganization(organization);
        // We then try to update the keycloak group of the organization
        try{
            authClient.updateGroup("Bearer " + jwt.getRawToken(), organizationGroup);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error
            throw e;
        }

        // If we're successful we call the organization Update and return the organization payload
        return (String) context.proceed();
    }

    @Override
    public String delete() throws Exception {
        // We get the id from the delete request and we read the organization resource:
        Organization organization = (Organization) fhirClient.read((String) context.getParameters()[0]);
        // We get the KeycloakGroupId associated with the organization.
        String keycloakGroupId = organization.getIdentifier().stream()
                .filter(id -> id.getUse() == Identifier.IdentifierUse.SECONDARY)
                .findFirst()
                .map(Identifier::getValue)
                .orElse(null);

        try{
            // We proceed with the deletion of the groups:
            groupClient.bulkDelete("Bearer " + jwt.getRawToken(), "group-id eq \""+ keycloakGroupId + "\"");

            // We try to delete the keycloak group associated with the organization
            authClient.deleteGroup("Bearer " + jwt.getRawToken(), keycloakGroupId).readEntity(String.class);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error
            throw e;
        }

        String response;
        try {
            // And then of the Fhir organization resource
            response = (String) context.proceed();
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error
            throw e;
        }



        return response;

    }

    private String getGroupPayload(String name){
        return "{\n" +
                "  \"resourceType\": \"Group\",\n" +
                "  \"type\": \"practitioner\",\n" +
                "  \"membership\": \"definitional\",\n" +
                "  \"name\": \""+name+"\"\n" +
                "}";
    }


}
