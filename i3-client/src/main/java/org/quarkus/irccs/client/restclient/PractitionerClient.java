package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Practitioner;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IPractitionerClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;


public class PractitionerClient extends CustomFhirContext  {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IPractitionerClient iPractitionerClient;

    public PractitionerClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();

        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iPractitionerClient = fhirRestClientConfiguration.newRestfulClient(IPractitionerClient.class);
    }

    public Practitioner getPractitionerById(IIdType theId) {
        return iPractitionerClient.getPractitionerById(theId);
    }


    public IIdType createPractitioner(Practitioner practitioner) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(practitioner)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }
    public Practitioner updatePractitioner(String id, Practitioner practitioner) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_PRACTITIONER, id);
        practitioner.setId(idType.toString());
        iGenericClient.update().resource(practitioner).execute();
        return practitioner;
    }

    public Bundle getAllPractitioners() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(Practitioner.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deletePractitionerById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_PRACTITIONER, id)).execute();

        return (OperationOutcome) response.getOperationOutcome();
    }

    public Bundle searchPractitionerByCode(String code) {
        return iGenericClient.search()
                .forResource(Practitioner.class)
                .where(Practitioner.IDENTIFIER.exactly().identifier(code))
                .returnBundle(Bundle.class)
                .execute();
    }

}
