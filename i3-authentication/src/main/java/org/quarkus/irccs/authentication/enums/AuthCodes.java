package org.quarkus.irccs.authentication.enums;

public enum AuthCodes {

    SUCCESS(200, "SUCCESS"),
    GENERIC_ERROR(0, "Generic ERROR"),

    PARSING_FAILED(2, "Parsing jwt failed"),

    LOGIN_ATTEMPT(3, "Login credentials wrong");

    public final int code;
    public final String label;


    AuthCodes(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return this.code;
    }

    public String getLabel() {
        return this.label;
    }
}
