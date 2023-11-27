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
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Patient;
import org.quarkus.irccs.client.interfaces.IPatientClient;

import static org.quarkus.irccs.common.constants.FhirConst.PATIENT_RESOURCE_TYPE;


@ApplicationScoped
public class PatientClient {
    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;
    private final IGenericClient iGenericClient;
    private final IPatientClient iPatientClient;

    @Inject
    PatientClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);
        // Create the client
        iPatientClient = ctx.newRestfulClient(IPatientClient.class, serverBase);
    }


    public Patient getPatientById(IIdType theId) {
        return iPatientClient.getPatientById(theId);
    }

    public OperationOutcome deletePatientById(IdType id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(id).execute();
        return (OperationOutcome) response.getOperationOutcome();
    }

    public IIdType createPatient(Patient patient) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(patient)
                .prettyPrint()
                .encodedJson()
                .execute();

        return outcome.getId();
    }

    public Patient updatePatient(String id, Patient patient) {
        IIdType idType = new IdType(PATIENT_RESOURCE_TYPE, id);
        patient.setId(idType.toString());
        iGenericClient.update().resource(patient).execute();
        return patient;
    }


    public Bundle getAllPatient() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(Patient.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();

    }

    public Bundle searchPatient(String city) {
        return iGenericClient.search()
                .forResource(Patient.class)
                .where(Patient.ADDRESS_CITY.matches().value(city))
                .returnBundle(Bundle.class)
                .execute();
    }
}
