package org.quarkus.irccs.annotations.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hl7.fhir.r5.model.Identifier;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.annotations.models.Group;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.List;
import java.util.Map;

public class GroupFlow extends Flow {

    public GroupFlow(FhirClient<?> fhirClient, InvocationContext context, AuthMicroserviceClient authClient, JsonWebToken jwt, HttpHeaders httpHeaders) {
        super(fhirClient, context, authClient, jwt, httpHeaders);
    }

    @Override
    public String create() throws Exception {
        // This adds the necessary groupsIds
        super.create();
        // This will contain the Keycloak Group.
        Group group;
        // We get the Fhir Group response, parse it, and save it in a variable:
        org.hl7.fhir.r5.model.Group fhirGroup =  (org.hl7.fhir.r5.model.Group) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.proceed());
        // We create a Keycloak Group from the Fhir response
        Group keycloakGroup = Group.groupFromFhirGroup(fhirGroup, fhirClient);
        // We add the Keycloak Group Organization Id (parentGroupId) which should be either in the headers or in the member claim.
        String organizationId;
        boolean isDataManager = false;
        boolean isOrgAdmin = false;
        if(null != this.jwt.getClaim("organizations-id")){
            organizationId = new ObjectMapper().readValue((new ObjectMapper()).readValue(this.jwt.getClaim("organizations-id").toString(), List.class).get(0).toString().replace("=", ":"), Map.class).get("keycloakId").toString();
        } else {
            // If we're getting the organizationId from the header, it means it's an internal call from an organization. It might be the DataManager group.
            organizationId = httpHeaders.getHeaderString("organizationId");
            isDataManager = Boolean.parseBoolean(httpHeaders.getHeaderString("isDataManager"));
            isOrgAdmin = Boolean.parseBoolean(httpHeaders.getHeaderString("isOrgAdmin"));
        }
        keycloakGroup.setParentGroupId(organizationId);
        // We also add the Fhir group id we just created to the keycloak group
        keycloakGroup.setGroupId(fhirGroup.getIdPart());
        // We add isDataManager property so to add a role on this group (If that's the case)
        keycloakGroup.setDataManager(isDataManager);
        // We add isOrgAdmin property so to add a role on this group (If that's the case)
        keycloakGroup.setOrgAdmin(isOrgAdmin);
        // We then try to create a keycloak group of the organization;
        try{
            group = authClient.createGroup("Bearer " + jwt.getRawToken(), keycloakGroup).readEntity(Group.class);
            /* The keycloak group has now been created, we'll add the ID to the FHIR Group */
            // The Id will be saved in the "Identifier" Fhir property as "SECONDARY" (The FIRST one being the actual FHIR ID).
            fhirGroup.setIdentifier(List.of(new Identifier().setUse(Identifier.IdentifierUse.SECONDARY).setValue(group.getId())));
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR group resource
            fhirClient.delete(fhirGroup.getIdPart());
            throw e;
        }

        try {
            // We now send our payload to add the keycloak ID.
            ((FhirClient<org.hl7.fhir.r5.model.Group>) fhirClient).update(fhirGroup);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR Group resource and the keycloak Group.
            fhirClient.delete(fhirGroup.getIdPart());
            authClient.deleteGroup("Bearer " + jwt.getRawToken(), group.getId());
            throw e;
        }

        // If we're successful we return the organization payload.
        return fhirClient.encodeResourceToString(fhirGroup);
    }
    @Override
    public String update() throws Exception {
        // We get the payload, parse it, and save it in a response variable:
        org.hl7.fhir.r5.model.Group group = (org.hl7.fhir.r5.model.Group) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.getParameters()[1]);
        // We add the id manually
        group.setId((String) context.getParameters()[0]);
        // We create a Group from the organization data
        Group keycloakGroup = Group.groupFromFhirGroup(group, fhirClient);
        // We then try to update the keycloak group of the organization
        try{
            authClient.updateGroup("Bearer " + jwt.getRawToken(), keycloakGroup);
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
        org.hl7.fhir.r5.model.Group group = (org.hl7.fhir.r5.model.Group) fhirClient.read((String) context.getParameters()[0]);
        // We get the KeycloakGroupId associated with the organization.
        String keycloakGroupId = group.getIdentifier().stream()
                .filter(id -> id.getUse() == Identifier.IdentifierUse.SECONDARY)
                .findFirst()
                .map(Identifier::getValue)
                .orElse(null);

        // We try to delete the keycloak group associated with the organization.
        try{
            authClient.deleteGroup("Bearer " + jwt.getRawToken(), keycloakGroupId).readEntity(String.class);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error
            throw e;
        }

        // We proceed with the deletion.
        return (String) context.proceed();

    }


}
