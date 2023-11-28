package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.Patient;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IPatientClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirQueryConst;
import static org.quarkus.irccs.common.constants.FhirConst.PATIENT_RESOURCE_TYPE;



public class PatientClient extends CustomFhirContext  {

    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IPatientClient iPatientClient;


    public PatientClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        // set a queryLimit
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        // Create the client
        iPatientClient = fhirRestClientConfiguration.newRestfulClient(IPatientClient.class);
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
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
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
