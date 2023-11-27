package org.quarkus.irccs.client.restclient;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.client.interfaces.IOrganizationClient;


import java.util.List;

@ApplicationScoped
public class OrganizationClient {

    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;

    private final IGenericClient iGenericClient;
    private final IOrganizationClient iOrganizationClient;

    @Inject
    OrganizationClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server.url") String serverBase, @ConfigProperty(name = "org.quarkus.irccs.fhir-server.timeout") Integer timeout) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        ctx.getRestfulClientFactory().setSocketTimeout(timeout);
        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);
        // Create the client
        iOrganizationClient = ctx.newRestfulClient(IOrganizationClient.class, serverBase);
    }


    public OperationOutcome deleteOrganizationById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType("Organization", id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public List<Organization> getOrganizationByName(String name) {
        return iOrganizationClient.getOrganizationByName(new StringType(name));
    }


    public Bundle getAllOrganizations() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(Organization.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }


    public Bundle searchOrganizationByCode(String code) {
        return iGenericClient.search()
                .forResource(Organization.class)
                .where(Organization.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }

    public Organization getOrganizationById( IIdType theId) {
        return iOrganizationClient.getOrganizationById(theId);
    }

    public Organization updateOrganization(String id, Organization organization) {
        IIdType idType = new IdType("Organization", id);
        organization.setId(idType.toString());
        iGenericClient.update().resource(organization).execute();
        return organization;
    }


    public IIdType createOrganization(Organization organization) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(organization)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

}
