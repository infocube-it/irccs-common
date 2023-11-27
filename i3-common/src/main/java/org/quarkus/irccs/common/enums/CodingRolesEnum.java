package org.quarkus.irccs.common.enums;

public enum CodingRolesEnum {

    ADMIN_GEST_FARMACO("ADMIN_GESTIONE_FARMACO", "AGF", "Amministratore"),
    AMMINISTRATORI("AMMINISTRATORE", "AMM", "Amministratore");

    public String system;
    public String code;
    public String display;


    CodingRolesEnum(String system, String code, String display) {
        this.system = system;
        this.code = code;
        this.display = display;
    }
}

