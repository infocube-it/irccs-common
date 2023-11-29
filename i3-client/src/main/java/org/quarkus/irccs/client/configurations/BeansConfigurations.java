package org.quarkus.irccs.client.configurations;

import ca.uhn.fhir.context.FhirContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quarkus.irccs.client.restclient.*;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;


@Singleton
public class BeansConfigurations {
    @Produces
    @ApplicationScoped
    public FhirRestClientConfiguration fhirRestClientConfiguration(@ConfigProperty(name = "org.quarkus.irccs.fhir-server") String serverBase, @ConfigProperty(name = "org.quarkus.irccs.query.limit") int queryLimit ) {
        return new FhirRestClientConfiguration(serverBase,queryLimit, FhirContext.forR5());
    }

    @Produces
    @ApplicationScoped
    public PatientClient patientClient(FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new PatientClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public AppointmentClient appointmentClient(FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new AppointmentClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public DiagnosticReportClient diagnosticReportClient(FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new DiagnosticReportClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public DataTypeClient dataTypeClient(FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new DataTypeClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public GroupClient groupClient(FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new GroupClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public PlanDefinitionClient planDefinitionClient(FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new PlanDefinitionClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public QuestionnaireClient questionnaireClient (FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new QuestionnaireClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public QuestionnaireResponseClient questionnaireResponseClient (FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new QuestionnaireResponseClient(fhirRestClientConfiguration);
    }

    @Produces
    @ApplicationScoped
    public ResearchStudyClient researchStudyClient (FhirRestClientConfiguration fhirRestClientConfiguration ) {
        return new ResearchStudyClient(fhirRestClientConfiguration);
    }

}