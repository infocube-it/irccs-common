package org.quarkus.irccs.client.restclient;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IOrganizationClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;


import java.util.List;

public class OrganizationClient extends CustomFhirContext  {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IOrganizationClient iOrganizationClient;


    public OrganizationClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        // Create the client
        iOrganizationClient = fhirRestClientConfiguration.newRestfulClient(IOrganizationClient.class);
    }


    public OperationOutcome deleteOrganizationById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_ORGANIZATION, id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public List<Organization> getOrganizationByName(String name) {
        return iOrganizationClient.getOrganizationByName(new StringType(name));
    }


    public Bundle getAllOrganizations() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
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
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_ORGANIZATION, id);
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
