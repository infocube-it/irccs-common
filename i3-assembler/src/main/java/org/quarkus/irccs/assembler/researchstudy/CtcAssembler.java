package org.quarkus.irccs.assembler.researchstudy;

import org.hl7.fhir.r5.model.DateType;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.StructureDefinition;

import java.util.ArrayList;
import java.util.List;

public class CtcAssembler {

    public static StructureDefinition createCtc(String medDRACode,String grade,String description,String date){

        String[] splittedDate = date.split("-");

        //(int theYear, int theMonth, int theDay)
        DateType dateType = new DateType(Integer.parseInt(splittedDate[2]),Integer.parseInt(splittedDate[1]),Integer.parseInt(splittedDate[0]));

        Extension extension = new Extension();
        List<Extension> extensions = new ArrayList<>();
        StructureDefinition structureDefinition = new StructureDefinition();

        structureDefinition.setName(medDRACode);
        structureDefinition.setTitle(grade);
        structureDefinition.setDescription(description);

        extension.setValue(dateType);

        extensions.add(extension);
        structureDefinition.setExtension(extensions);

        return  structureDefinition;
    }



}
