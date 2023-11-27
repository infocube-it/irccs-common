package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.quarkus.irccs.client.interfaces.IMedicationAdministrationClient;


@ApplicationScoped
public class MedicationAdministrationClient {

    private final IGenericClient iGenericClient;

    private final IMedicationAdministrationClient iMedicationAdministrationClient;


    @Inject
    MedicationAdministrationClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext fhirContext = FhirContext.forR4();


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
