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
import org.quarkus.irccs.client.context.CustomFhirContext;
import org.quarkus.irccs.client.interfaces.IAppointmentClient;
import org.quarkus.irccs.client.interfaces.ICarePlanClient;
import org.quarkus.irccs.client.interfaces.IProcedureClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.quarkus.irccs.common.constants.FhirConst;
import org.quarkus.irccs.common.constants.FhirQueryConst;

public class DataTypeClient extends CustomFhirContext {
    private final int queryLimit;

    private final IGenericClient iGenericClient;

    private final ICarePlanClient iCarePlanClient;

    private final IProcedureClient iProcedureClient;


    private final IAppointmentClient iAppointmentClient;


    public DataTypeClient(FhirRestClientConfiguration fhirRestClientConfiguration) {
        super(fhirRestClientConfiguration.getFhirContext());
        this.queryLimit = fhirRestClientConfiguration.getQueryLimit();


        //Create a Generic Client without map
        iGenericClient = fhirRestClientConfiguration.getiGenericClient();

        iCarePlanClient = fhirRestClientConfiguration.newRestfulClient(ICarePlanClient.class);
        iProcedureClient = fhirRestClientConfiguration.newRestfulClient(IProcedureClient.class);
        iAppointmentClient = fhirRestClientConfiguration.newRestfulClient(IAppointmentClient.class);
    }


    public Procedure getProcedureById(IIdType theId) {
        return iProcedureClient.getProcedureById(theId);
    }

    public CarePlan getCarePlanById(IIdType theId){
        return  iCarePlanClient.getCarePlanById(theId);
    }



    public Bundle getAllCarePlan() {
        SortSpec sortSpec = new SortSpec(FhirQueryConst.LAST_UPDATE,
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



    public void deleteCarePlan(String id) {
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
