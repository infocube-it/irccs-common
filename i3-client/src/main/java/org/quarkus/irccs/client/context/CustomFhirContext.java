package org.quarkus.irccs.client.context;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;


public class CustomFhirContext {
    public static IParser getJsonParser() {
        FhirContext ctx = FhirContext.forR5();
        IParser jsonParser = ctx.newJsonParser();
        jsonParser.setPrettyPrint(true);
        return  jsonParser;
    }
}
