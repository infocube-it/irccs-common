package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.Appointment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentAssembler {

    //appuntamento visita sperimentali
    public static Appointment createAppointment() {
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);
        appointment.setCreated(new Date());
        appointment.setDescription("Appuntamento di Follow-up");

       return appointment;
    }


    public static Appointment createAppointmentOfResearchStudy() {
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.AppointmentStatus.NOSHOW);
        List<Appointment.AppointmentParticipantComponent> participantComponentList = new ArrayList<>();
        participantComponentList.add(
                new Appointment.AppointmentParticipantComponent(Appointment.ParticipationStatus.ACCEPTED)
        );


        appointment.setParticipant(participantComponentList);
        return appointment;
    }


    public static Appointment createEmptyAppointment() {
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.AppointmentStatus.NOSHOW);
        return appointment;
    }

}
