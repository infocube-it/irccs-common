package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Group;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IGroupClient;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;


public class GroupClient extends CustomFhirContext {

    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IGroupClient iGroupClient;


    public GroupClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.queryLimit = queryLimit;
        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);

        iGroupClient = fhirContext.newRestfulClient(IGroupClient.class, serverBase);

    }

    public Group updateGroup(String id, Group group) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_GROUP, id);
        group.setId(idType.toString());
        iGenericClient.update().resource(group).execute();
        return group;
    }

    public Bundle getAllGroups() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
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
