package org.quarkus.irccs.client.restclient.model;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;

public class FhirRestClientConfiguration {
    private final FhirContext fhirContext;
    private final String serverBase;
    private final int queryLimit;
    private final IGenericClient iGenericClient;


    public FhirRestClientConfiguration(String serverBase, int queryLimit, FhirContext fhirContext) {
        this.serverBase = serverBase;
        this.queryLimit = queryLimit;
        this.fhirContext = fhirContext;

        //Create a Generic Client without map
        iGenericClient = fhirContext.newRestfulGenericClient(serverBase);
    }

    public FhirContext getFhirContext() {
        return fhirContext;
    }

    public String getServerBase() {
        return serverBase;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public IGenericClient getiGenericClient() {
        return iGenericClient;
    }

    public <T extends IRestfulClient> T newRestfulClient(Class<T> theClientType) {
        return this.fhirContext.getRestfulClientFactory().newClient(theClientType, serverBase);
    }

}