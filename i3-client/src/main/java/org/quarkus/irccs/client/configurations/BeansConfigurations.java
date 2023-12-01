package org.quarkus.irccs.client.configurations;

import ca.uhn.fhir.context.FhirContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.quarkus.irccs.client.restclient.*;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;

import java.lang.reflect.ParameterizedType;


@Singleton
public class BeansConfigurations {
    @Produces
    @Dependent
    @SuppressWarnings("unchecked")
    public <T extends IBaseResource> FhirResourceClient<T> create(InjectionPoint injectionPoint, FhirRestClientConfiguration fhirRestClientConfiguration) {
        Class<T> type = (Class<T>) ((ParameterizedType) injectionPoint.getType()).getActualTypeArguments()[0];
        return new FhirResourceClient<>(fhirRestClientConfiguration, type);
    }

    @Produces
    @ApplicationScoped
    public FhirRestClientConfiguration fhirRestClientConfiguration(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase, @ConfigProperty(name = "org.quarkus.irccs.query.limit") int queryLimit ) {
        return new FhirRestClientConfiguration(serverBase,queryLimit, FhirContext.forR5());
    }

}


