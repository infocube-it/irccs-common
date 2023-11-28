package org.quarkus.irccs.common.constants;

public class RoutingPathConst {
    private static final String ROUTING_PREFIX = "/";
    public static final String ROUTING_PATIENT = ROUTING_PREFIX + FhirConst.PATIENT_RESOURCE_TYPE;
    public static final String ROUTING_RESEARCHSTUDY= ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_RESEARCHSTUDY;
    public static final String ROUTING_QUESTIONNAIRE= ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_QUESTIONNAIRE;
    public static final String ROUTING_QUESTIONNAIRE_RESPONSE= ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_QUESTIONNAIRE_RESPONSE;
    public static final String ROUTING_PLAN_DEFINITION= ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_PLAN_DEFINITION;
    public static final String ROUTING_DIAGNOSTIC_REPORT =ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_DIAGNOSTIC_REPORT;
    public static final String ROUTING_PROCEDURE = ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_PROCEDURE;
    public static final String ROUTING_CAREPLAN = ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_CAREPLAN;
    public static final String ROUTING_GROUP = ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_GROUP;
    public static final String ROUTING_APPOINTMENT = ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_APPOINTMENT;
    public static final String ROUTING_ORGANIZATION=ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_ORGANIZATION;
    public static final String ROUTING_PRACTITIONER = ROUTING_PREFIX + FhirConst.RESOURCE_TYPE_PRACTITIONER;

}



