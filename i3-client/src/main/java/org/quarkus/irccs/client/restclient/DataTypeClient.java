package org.quarkus.irccs.client.restclient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r5.model.*;
import org.quarkus.irccs.client.interfaces.IAppointmentClient;
import org.quarkus.irccs.client.interfaces.ICarePlanClient;
import org.quarkus.irccs.client.interfaces.IProcedureClient;
import org.quarkus.irccs.common.constants.FhirConst;


@ApplicationScoped
public class DataTypeClient {

    @ConfigProperty(name = "org.quarkus.irccs.query.limit")
    int queryLimit;

    private final IGenericClient iGenericClient;

    private final ICarePlanClient iCarePlanClient;

    private final IProcedureClient iProcedureClient;


    private final IAppointmentClient iAppointmentClient;

    @Inject
    DataTypeClient(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase) {
        // Init Context
        FhirContext ctx = FhirContext.forR5();

        ctx.getRestfulClientFactory().setSocketTimeout(30000);

        //Create a Generic Client without map
        iGenericClient = ctx.newRestfulGenericClient(serverBase);

        iCarePlanClient = ctx.newRestfulClient(ICarePlanClient.class, serverBase);
        iProcedureClient = ctx.newRestfulClient(IProcedureClient.class, serverBase);

        iAppointmentClient = ctx.newRestfulClient(IAppointmentClient.class, serverBase);

    }


    public Procedure getProcedureById(IIdType theId) {
        return iProcedureClient.getProcedureById(theId);
    }

    public CarePlan getCarePlanById(IIdType theId){
        return  iCarePlanClient.getCarePlanById(theId);
    }



    public Bundle getAllCarePlan() {
        SortSpec sortSpec = new SortSpec("_lastUpdated",
                SortOrderEnum.DESC);
        return
                iGenericClient.search()
                        .forResource(CarePlan.class)
                        .count(queryLimit)
                        .sort(sortSpec)
                        .returnBundle(Bundle.class)
                        .execute();
    }

    public IIdType createProcedure(Procedure procedure) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(procedure)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }


    public IIdType createCarePlan(CarePlan carePlan) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(carePlan)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }


    @DELETE
    @Path("/{id}")
    public void deleteCarePlan(@PathParam("id") String id) {
        MethodOutcome response =
                iGenericClient.delete().resourceById(new IdType(FhirConst.RESOURCE_TYPE_GROUP, id)).execute();
        //return (OperationOutcome) response.getOperationOutcome();
    }

    public IIdType createQuestionnaire(Questionnaire questionnaire) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(questionnaire)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

    public IIdType updateCarePlan(CarePlan carePlan) {
        MethodOutcome outcome = iGenericClient.update()
                .resource(carePlan)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

    public CarePlan updateCarePlan(String id, CarePlan carePlan) {
        IIdType idType = new IdType(FhirConst.RESOURCE_TYPE_CAREPLAN, id);
        carePlan.setId(idType.toString());
        iGenericClient.update().resource(carePlan).execute();
        return carePlan;
    }

    public IIdType createAppointment(Appointment appointment) {
        MethodOutcome outcome = iGenericClient.create()
                .resource(appointment)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId();
    }

    public Appointment getAppointmentById(IIdType theId) {
        return iAppointmentClient.getAppointmentById(theId);
    }
}
