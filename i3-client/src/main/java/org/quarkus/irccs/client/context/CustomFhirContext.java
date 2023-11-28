package org.quarkus.irccs.client.context;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBaseResource;


public class CustomFhirContext {
    protected IParser iParser;

    //todo:: please refactoring this, constructor and post constructor doesn't' work
    public void init() {
        if(this.iParser == null) {
            FhirContext fhirContext = FhirContext.forR5();
            this.iParser = fhirContext.newJsonParser();
        }
    }

    public String encodeResourceToString(IBaseResource iBaseResource) {
        this.init();
        iParser.setPrettyPrint(true);
        return  iParser.encodeResourceToString(iBaseResource);
    }

    public  <T extends IBaseResource> T parseResource(Class<T> cast, String strObject) {
        this.init();
        return iParser.parseResource(cast, strObject);
    }

}
