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
import org.hl7.fhir.r5.model.Appointment;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.IdType;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.quarkus.irccs.client.interfaces.IAppointmentClient;
import org.quarkus.irccs.common.constants.FhirConst;

@ApplicationScoped
public class AppointmentClient {
    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;
    private final IGenericClient iGenericClient;
    private final IAppointmentClient iAppointmentClient;

    @Inject
    AppointmentClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();
        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iAppointmentClient = ctx.newRestfulClient(IAppointmentClient.class, serverBase);

    }

    public Appointment updateAppointment(String id, Appointment appointment) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_APPOINTMENT, id);
        appointment.setId(idType.toString());
        iGenericClient.update().resource(appointment).execute();
        return appointment;
    }

    public Bundle getAllAppointments() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
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
