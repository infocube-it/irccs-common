package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.Appointment;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IAppointmentClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;


public class AppointmentClient extends CustomFhirContext{
    private final int queryLimit;
    private final IGenericClient iGenericClient;
    private final IAppointmentClient iAppointmentClient;

    public AppointmentClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();
        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();
        iAppointmentClient = fhirRestClientConfiguration.newRestfulClient(IAppointmentClient.class);
    }

    public Appointment updateAppointment(String id, Appointment appointment) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_APPOINTMENT, id);
        appointment.setId(idType.toString());
        iGenericClient.update().resource(appointment).execute();
        return appointment;
    }

    public Bundle getAllAppointments() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(Appointment.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public OperationOutcome deleteAppointmentById(String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_APPOINTMENT, id)).execute();
        return (OperationOutcome) response.getOperationOutcome();
    }

    public Appointment getAppointmentById(IIdType theId) {
        return iAppointmentClient.getAppointmentById(theId);
    }

    public IIdType createAppointment(Appointment appointment) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(appointment)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }
}
