package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IMedicationAdministrationClient;



public class MedicationAdministrationClient extends CustomFhirContext {
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IMedicationAdministrationClient iMedicationAdministrationClient;

    public MedicationAdministrationClient(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.queryLimit = queryLimit;

        fhirContext.getRestfulClientFactory().setSocketTimeout(30000);

        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);

        iMedicationAdministrationClient = fhirContext.newRestfulClient(IMedicationAdministrationClient.class, serverBase);
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
