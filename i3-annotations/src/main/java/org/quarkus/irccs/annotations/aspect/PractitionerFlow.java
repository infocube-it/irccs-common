package org.quarkus.irccs.annotations.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hl7.fhir.r5.model.*;
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
            try{
                orgReq = new ObjectMapper().readValue((new ObjectMapper()).readValue(this.jwt.getClaim("organizations-id").toString(), List.class).get(0).toString().replace("=", ":"), Map.class).get("keycloakId").toString();
            }
            // If we're here it means the user creating a Practitioner does not have an organization. Nevertheless, it has the ability to create a Practitioner: It's an Admin.
            catch (Exception e){}

        }
        String unitName =  getExtensionValue(fhirPractitioner, "unitName");
        String role =  getExtensionValue(fhirPractitioner, "role");
        String structure =  getExtensionValue(fhirPractitioner, "structure");
        super.create();
        Practitioner practitioner = fhirClient.parseResource(Practitioner.class, (String) context.getParameters()[0]);
        List<Extension> practitionerExtensionList = practitioner.getExtension();
        practitionerExtensionList.add(new Extension("group_ids", new StringType(orgReq)));
        practitioner.setExtension(practitionerExtensionList);
        context.getParameters()[0] = fhirClient.encodeResourceToString(practitioner);
        Practitioner createdPractitioner = (Practitioner) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.proceed());
        // We create a Keycloak Group from the Fhir response
        User keycloakUser = User.fromPractitioner(createdPractitioner,psw, orgReq, unitName, role, structure);
        try{
            user = authClient.createUser("Bearer " + jwt.getRawToken(), keycloakUser).readEntity(User.class);
            /* The keycloak group has now been created, we'll add the ID to the FHIR Group */
            // The Id will be saved in the "Identifier" Fhir property as "SECONDARY" (The FIRST one being the actual FHIR ID).
            createdPractitioner.setIdentifier(List.of(new Identifier().setUse(Identifier.IdentifierUse.SECONDARY).setValue(user.getId())));
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR group resource
            fhirClient.delete(createdPractitioner.getIdPart());
            throw e;
        }

        try {
            // We now send our payload to add the keycloak ID.
            ((FhirClient<Practitioner>) fhirClient).update(createdPractitioner);
        } catch (ClientWebApplicationException e){
            // If we don't succeed then we return an error and delete the FHIR Group resource and the keycloak Group.
            fhirClient.delete(createdPractitioner.getIdPart());
            authClient.deleteGroup("Bearer " + jwt.getRawToken(), user.getId());
            throw e;
        }

        // If we're successful we return the organization payload.
        return fhirClient.encodeResourceToString(createdPractitioner);
    }
    @Override
    public String update() throws Exception {
        // We get the payload, parse it, and save it in a response variable:
        Practitioner practitioner = (Practitioner) fhirClient.parseResource(fhirClient.getResourceType(), (String) context.getParameters()[1]);
        // We add the id manually
        practitioner.setId((String) context.getParameters()[0]);
        // We create a Group from the organization data
        User user = User.fromPractitioner(practitioner, null, null, null, null, null);
        user.setEnabled(true);
        // We then try to update the keycloak group of the organization
        try{
            authClient.updateUser("Bearer " + jwt.getRawToken(), user);
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
        Practitioner practitioner = (Practitioner) fhirClient.read((String) context.getParameters()[0]);
        // We get the KeycloakGroupId associated with the organization.
        String keycloakPractitionerId = practitioner.getIdentifier().stream()
                .filter(id -> id.getUse() == Identifier.IdentifierUse.SECONDARY)
                .findFirst()
                .map(Identifier::getValue)
                .orElse(null);

        // We try to delete the keycloak group associated with the organization.
        try{
            authClient.deleteUser("Bearer " + jwt.getRawToken(), keycloakPractitionerId).readEntity(String.class);
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
