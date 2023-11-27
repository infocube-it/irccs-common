package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Procedure;

public class ProcedureAssembler {


    //Visita sperimentali
    public static Procedure createProcedure() {
       /* todo:: expected map this

       Nome
        Distanza da perc (gg)
        Ordine sequenza
        Diviso per gg
        Indica (min/max)
        parti del periodo n.

        numero cicli (required)
        parti del ciclo m. (required)
        giorni durata ciclo (required)

        un. misura cicli

        */

       Procedure procedure = new Procedure();
       procedure.setStatus(Enumerations.EventStatus.INPROGRESS);


       return procedure;
    }

}
