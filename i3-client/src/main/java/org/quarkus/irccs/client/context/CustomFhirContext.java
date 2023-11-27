package org.quarkus.irccs.client.context;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomFhirContext {

    public IParser getJsonParser() {
        FhirContext ctx = FhirContext.forR5();
        IParser jsonParser = ctx.newJsonParser();
        jsonParser.setPrettyPrint(true);
        return  jsonParser;
    }
}
