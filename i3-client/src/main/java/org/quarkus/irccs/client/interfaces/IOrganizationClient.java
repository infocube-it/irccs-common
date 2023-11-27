package org.quarkus.irccs.client.interfaces;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Organization;
import org.hl7.fhir.r5.model.StringType;

import java.util.List;


public interface IOrganizationClient extends IBasicClient {

    @Read()
    Organization getOrganizationById(@IdParam IIdType theId);

    @Search()
    List<Organization> getOrganizationByName(@RequiredParam(name = Organization.SP_NAME) StringType name);
}