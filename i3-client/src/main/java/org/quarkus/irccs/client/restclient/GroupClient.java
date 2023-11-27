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
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Group;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.interfaces.IGroupClient;
import org.quarkus.irccs.common.constants.FhirConst;


@ApplicationScoped
public class GroupClient {
    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;
    private final IGenericClient iGenericClient;
    private final IGroupClient iGroupClient;

    @Inject
    GroupClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iGroupClient = ctx.newRestfulClient(IGroupClient.class, serverBase);

    }

    public Group updateGroup(String id, Group group) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_GROUP, id);
        group.setId(idType.toString());
        iGenericClient.update().resource(group).execute();
        return group;
    }

    public Bundle getAllGroups() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(Group.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deleteGroupById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_GROUP, id)).execute();
        return (OperationOutcome) response.getOperationOutcome();
    }

    public Group getGroupById(IIdType theId) {
        return iGroupClient.getGroupById(theId);
    }

    public IIdType createGroup(Group group) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(group)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }
}
