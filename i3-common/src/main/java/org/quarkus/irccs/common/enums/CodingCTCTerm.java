package org.quarkus.irccs.common.enums;

public enum CodingCTCTerm {
    BODY_ODOR("https://ncit.nci.nih.gov/", "Body odor");

    public String system;
    public String display;


   // public static CodingCTCTerm getValueBycTCAETerm(String cTCAETerm){
   //     switch (cTCAETerm.toLowerCase()){
   //         case "anemia":
   //             return CodingCTCTerm.BODY_ODOR;
   //         case "..":
   //             return ..
   //
   //     }
   // }

    CodingCTCTerm(String system, String display){
        this.system = system;
        this.display = display;
    }

}
