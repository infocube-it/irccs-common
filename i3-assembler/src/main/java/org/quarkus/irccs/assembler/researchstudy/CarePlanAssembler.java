package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarePlanAssembler {

    //Initialize a Terapie
    public static CarePlan initializeCarePlan(Procedure procedure) {
        CarePlan carePlan = new CarePlan();

        List<CarePlan.CarePlanActivityComponent> carePlanActivityComponentList = new ArrayList<>();
        CarePlan.CarePlanActivityComponent carePlanActivityComponent = new CarePlan.CarePlanActivityComponent();

        List<CodeableReference> codeableReferencesList = new ArrayList<>();
        CodeableReference codeableReference = new CodeableReference(new Reference(procedure.getId()));
        codeableReferencesList.add(codeableReference);
        carePlanActivityComponent.setPerformedActivity(codeableReferencesList);

        carePlanActivityComponentList.add(carePlanActivityComponent);
        carePlan.setActivity(carePlanActivityComponentList);
        return carePlan;
    }



    public static CarePlan createCarePlanOfPlanDefinition(Patient patient, Questionnaire questionnaire, Appointment appointment) {
        CarePlan carePlan = new CarePlan();
        List<CarePlan.CarePlanActivityComponent> activity = new ArrayList<>();

        carePlan.setTitle("Fase 1 - Anagrafica Paziente");
        carePlan.setIntent(CarePlan.CarePlanIntent.PROPOSAL);
        carePlan.setStatus(Enumerations.RequestStatus.ACTIVE);
        carePlan.setSubject(new Reference(patient));

        carePlan.setDescription("Descrizione della fase e crf associate");
        CarePlan.CarePlanActivityComponent carePlanActivityComponent = new CarePlan.CarePlanActivityComponent();
        carePlanActivityComponent.setPlannedActivityReference(new Reference(appointment));
        List<CodeableReference>  codeableReferences = new ArrayList<>();
        Reference questionnaireReference = new Reference(questionnaire);
        questionnaireReference.setIdentifier(questionnaire.getIdentifier().stream().findFirst().get());

        CodeableReference codeableReference = new CodeableReference(questionnaireReference);
        codeableReferences.add(codeableReference);
        carePlanActivityComponent.setPerformedActivity(codeableReferences);
        activity.add(carePlanActivityComponent);
        carePlan.setActivity(activity);
        return carePlan;
    }




    public static CarePlan initializeCarePlanWithAnnotationAndQuestionnaire(Annotation annotation, List<Questionnaire> questionnaires) {
        CarePlan carePlan = initializeCarePlan(questionnaires);
        List<Annotation> annotations = new ArrayList<>();

        List<CarePlan.CarePlanActivityComponent> carePlanActivityComponents = carePlan.getActivity();
        CarePlan.CarePlanActivityComponent carePlanActivityComponent = new CarePlan.CarePlanActivityComponent();


        annotations.add(annotation);
        carePlanActivityComponent.setProgress(annotations);
        carePlanActivityComponents.add(carePlanActivityComponent);
        carePlan.setActivity(carePlanActivityComponents);
        return carePlan;
    }


    public static CarePlan initializeCarePlan(List<Questionnaire> questionnaires) {
        CarePlan carePlan = new CarePlan();

        List<CarePlan.CarePlanActivityComponent> carePlanActivityComponentList = new ArrayList<>();
        CarePlan.CarePlanActivityComponent carePlanActivityComponent = new CarePlan.CarePlanActivityComponent();



        List<CodeableReference> codeableReferencesList = new ArrayList<>();
        for(Questionnaire questionnaire: questionnaires) {
            Reference reference = new Reference(questionnaire.getId());
            Optional<Identifier> questionnaireIdentifier = questionnaire.getIdentifier().stream().findFirst();
            questionnaireIdentifier.ifPresent(reference::setIdentifier);

            CodeableReference codeableReference = new CodeableReference(reference);
            codeableReferencesList.add(codeableReference);
        }

        carePlanActivityComponent.setPerformedActivity(codeableReferencesList);
        carePlanActivityComponentList.add(carePlanActivityComponent);
        carePlan.setActivity(carePlanActivityComponentList);
        return carePlan;
    }

    public static CarePlan initializeCarePlan(Questionnaire questionnaire) {
        List<Questionnaire> questionnaires = new ArrayList<>();
        questionnaires.add(questionnaire);
        return initializeCarePlan(questionnaires);
    }

    public static CarePlan updateCarePlan(CarePlan carePlan, Procedure procedure) {

        CarePlan.CarePlanActivityComponent carePlanActivityComponent = new CarePlan.CarePlanActivityComponent();

        List<CodeableReference> codeableReferencesList = new ArrayList<>();
        CodeableReference codeableReference = new CodeableReference(new Reference(procedure.getId()));
        codeableReferencesList.add(codeableReference);
        carePlanActivityComponent.setPerformedActivity(codeableReferencesList);

        List<CarePlan.CarePlanActivityComponent> activity = carePlan.getActivity();

        activity.add(carePlanActivityComponent);
        return carePlan;
    }

    public static CarePlan updateCarePlan(CarePlan carePlan, Appointment appointment) {

        CarePlan.CarePlanActivityComponent carePlanActivityComponent = new CarePlan.CarePlanActivityComponent();
//TODO verificare secondo me è giusto metterli in planned e non performedactivity e poi non bisogna in update ricreare la lista di codeable/reference
        List<CodeableReference> codeableReferencesList = new ArrayList<>();
        CodeableReference codeableReference = new CodeableReference(new Reference(appointment.getId()));
        codeableReferencesList.add(codeableReference);
        carePlanActivityComponent.setPerformedActivity(codeableReferencesList);

        List<CarePlan.CarePlanActivityComponent> activity = carePlan.getActivity();
        activity.add(carePlanActivityComponent);
        return carePlan;
    }


}
