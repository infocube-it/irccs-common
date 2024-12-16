package org.quarkus.irccs.annotations.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.Identifier;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.Resource;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.quarkus.irccs.annotations.models.Group;
import org.quarkus.irccs.annotations.models.User;
import org.quarkus.irccs.annotations.models.clients.AuthMicroserviceClient;
import org.quarkus.irccs.client.restclient.FhirClient;

import java.util.List;
import java.util.Map;

public class PractitionerFlow extends Flow {

    public PractitionerFlow(FhirClient<?> fhirClient, InvocationContext context, AuthMicroserviceClient authClient, JsonWebToken jwt, HttpHeaders httpHeaders) {
        super(fhirClient, context, authClient, jwt, httpHeaders);
    }

    @Override
    public String create() throws Exception {
        // This will contain the Keycloak Group.
        User user;
        // We get the Fhir Group response, parse it, and save it in a variable:
        Practitioner fhirPractitioner =  (Practitioner) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.getParameters()[0]);
        String psw = getExtensionValue(fhirPractitioner, "password");
        String orgReq =  getExtensionValue(fhirPractitioner, "organizationRequest");
        if(null == orgReq){
            orgReq = new ObjectMapper().readValue((new ObjectMapper()).readValue(this.jwt.getClaim("organizations-id").toString(), List.class).get(0).toString().replace("=", ":"), Map.class).get("keycloakId").toString();
        }
        String unitName =  getExtensionValue(fhirPractitioner, "unitName");
        String role =  getExtensionValue(fhirPractitioner, "role");
        String structure =  getExtensionValue(fhirPractitioner, "structure");
        super.create();
        fhirPractitioner =  (Practitioner) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.proceed());
        // We create a Keycloak Group from the Fhir response
        User keycloakUser = User.fromPractitioner(fhirPractitioner,psw, orgReq, unitName, role, structure);
        try{
            user = authClient.createUser("Bearer " + jwt.getRawToken(), keycloakUser).readEntity(User.class);
            /* The keycloak group has now been created, we'll add the ID to the FHIR Group */
            // The Id will be saved in the "Identifier" Fhir property as "SECONDARY" (The FIRST one being the actual FHIR ID).
            fhirPractitioner.setIdentifier(List.of(new Identifier().setUse(Identifier.IdentifierUse.SECONDARY).setValue(user.getId())));
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR group resource
            fhirClient.delete(fhirPractitioner.getIdPart());
            throw e;
        }

        try {
            // We now send our payload to add the keycloak ID.
            ((FhirClient<Practitioner>) fhirClient).update(fhirPractitioner);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR Group resource and the keycloak Group.
            fhirClient.delete(fhirPractitioner.getIdPart());
            authClient.deleteGroup("Bearer " + jwt.getRawToken(), user.getId());
            throw e;
        }

        // If we're successful we return the organization payload.
        return fhirClient.encodeResourceToString(fhirPractitioner);
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

    private String getExtensionValue(Practitioner resource, String url) {
        Extension extension = resource.getExtensionByUrl(url);
        return (extension != null && extension.getValueStringType() != null)
                ? extension.getValueStringType().getValue()
                : null;
    }


}
