package org.quarkus.irccs.client.context;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import jakarta.ws.rs.core.MultivaluedMap;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.stream.Collectors;


public class CustomFhirContext {
    protected final IParser iParser;

    protected CustomFhirContext() {
        this.iParser = null;
    }
    protected CustomFhirContext(FhirContext fhirContext) {
        this.iParser = fhirContext.newJsonParser();
    }

    public String encodeResourceToString(IBaseResource iBaseResource) {
        iParser.setPrettyPrint(true);
        return  iParser.encodeResourceToString(iBaseResource);
    }

    public  <T extends IBaseResource> T parseResource(Class<T> cast, String strObject) {
        return iParser.parseResource(cast, strObject);
    }

    public String convertToQueryString(MultivaluedMap<String, String> params) {
        return params.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .map(v -> e.getKey() + "=" + v))
                .collect(Collectors.joining("&")).replace(' ', '+');
    }

}
