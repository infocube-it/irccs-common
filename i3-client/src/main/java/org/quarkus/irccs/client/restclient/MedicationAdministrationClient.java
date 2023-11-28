package org.quarkus.irccs.client.restclient;


import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IMedicationAdministrationClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;


public class MedicationAdministrationClient extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IMedicationAdministrationClient iMedicationAdministrationClient;

    public MedicationAdministrationClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();

        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iMedicationAdministrationClient = fhirRestClientConfiguration.newRestfulClient(IMedicationAdministrationClient.class);
    }

    public MedicationAdministration getMedicationAdministrationById(IIdType theId) {
        return iMedicationAdministrationClient.getMedicationAdministrationById(theId);
    }


    public IIdType createMedicationAdministration(MedicationAdministration  medicationAdministration) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(medicationAdministration)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

}
