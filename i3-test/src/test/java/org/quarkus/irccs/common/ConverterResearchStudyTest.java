package org.quarkus.irccs.common;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.Constants;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.ConfigProvider;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.convertors.conv40_50.resources40_50.Practitioner40_50;
import org.hl7.fhir.convertors.factory.VersionConvertorFactory_40_50;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.ResearchStudy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quarkus.irccs.client.converters.FHIRResearchStudyConverter;
import org.quarkus.irccs.client.restclient.FhirClient;
import org.quarkus.irccs.client.restclient.model.FhirRestClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ConverterResearchStudyTest {
    public static final String ORGANIZATION_FOR_ONCOLOGY_STUDY = "Organization for Oncology Study";

    private static final Logger log = LoggerFactory.getLogger(ConverterResearchStudyTest.class);
    // Creazione del contesto per FHIR R4
    FhirContext ctxR4 = FhirContext.forR4();

    // Creazione del contesto per FHIR R5
    FhirContext ctxR5 = FhirContext.forR5();

    FhirRestClientConfiguration fhirRestClientConfigurationR4 = new FhirRestClientConfiguration(
            getFhirUrl(), 100, ctxR4);

    FhirRestClientConfiguration fhirRestClientConfigurationR5 = new FhirRestClientConfiguration(
            getFhirUrl(), 100, ctxR5);

    FhirClient<org.hl7.fhir.r4.model.ResearchStudy> fhirClientR4 = new FhirClient<>(
            fhirRestClientConfigurationR4, org.hl7.fhir.r4.model.ResearchStudy.class);

    FhirClient<ResearchStudy> fhirClientR5 = new FhirClient<>(
            fhirRestClientConfigurationR5, ResearchStudy.class);

    // Parser JSON di HAPI FHIR
    IParser jsonParserR5 = ctxR5.newJsonParser();

    // Parser JSON di HAPI FHIR
    IParser jsonParserR4 = ctxR4.newJsonParser();

    FHIRResearchStudyConverter converter = new FHIRResearchStudyConverter(new BaseAdvisor_40_50());

    @Test
    void researchStudyConverterTest() throws FHIRFormatError, IOException {
        log.info("ResearchStudyConverterTest");

        InputStream r5_input = this.getClass().getResourceAsStream("/researchstudy_50_example.json");
        if (r5_input == null) {
            log.error("Failed to load resource: researchstudy_50_example.json");
            throw new IllegalStateException("Resource not found");
        }

        ResearchStudy researchStudyR5 = jsonParserR5.parseResource(ResearchStudy.class,
                r5_input);
        assertNotNull(researchStudyR5);

        // Converte da R5 a R4
        org.hl7.fhir.r4.model.ResearchStudy researchStudyR4 = converter.convertR5ToR4(researchStudyR5);
        assertNotNull(researchStudyR4, "Conversione R5 -> R4 fallita");

        // Converte da R4 a R5
        ResearchStudy convertedBackR5 = converter.convertR4ToR5(researchStudyR4);
        assertNotNull(convertedBackR5, "Conversione R4 -> R5 fallita");

        assertEquals(jsonParserR5.encodeResourceToString(convertedBackR5),
                jsonParserR5.encodeResourceToString(researchStudyR5),
                "Risorse uguali");
    }

    private static String getFhirUrl() {
        return ConfigProvider.getConfig().getConfigValue("org.quarkus.irccs.fhir-server").getValue();
    }

    @Test
    void convertR4ToR5() {
        org.hl7.fhir.r4.model.ResearchStudy studyR4 = new org.hl7.fhir.r4.model.ResearchStudy();
        studyR4.setId("123");
        studyR4.setTitle("Test Research");
        studyR4.setStatus(org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus.ACTIVE);

        org.hl7.fhir.r5.model.ResearchStudy studyR5 = converter.convertR4ToR5(studyR4);

        assertNotNull(studyR5);
        assertEquals("123", studyR5.getId());
        assertEquals("Test Research", studyR5.getTitle());
        assertEquals(Enumerations.PublicationStatus.ACTIVE, studyR5.getStatus());
    }

    @Test
    void convertR5ToR4() {
        org.hl7.fhir.r5.model.ResearchStudy studyR5 = new org.hl7.fhir.r5.model.ResearchStudy();
        studyR5.setId("456");
        studyR5.setTitle("Converted Study");

        org.hl7.fhir.r4.model.ResearchStudy studyR4 = converter.convertR5ToR4(studyR5);

        assertNotNull(studyR4);
        assertEquals("456", studyR4.getId());
        assertEquals("Converted Study", studyR4.getTitle());
    }

    @Test
    void convertExtensionR4toR5() {
        org.hl7.fhir.r4.model.Extension extR4 = new org.hl7.fhir.r4.model.Extension();
        extR4.setUrl("http://example.com");

        Extension extR5 = converter.convertExtensionR4toR5(extR4);

        assertNotNull(extR5);
        assertEquals("http://example.com", extR5.getUrl());
    }

    @Test
    void saveGetConvertSimpleResearchStudyOnFhir() {
        InputStream r5_input = this.getClass().getResourceAsStream("/researchstudy_50_example.json");
        if (r5_input == null) {
            log.error("Failed to load resource: researchstudy_50_example.json");
            throw new IllegalStateException("Resource not found");
        }

        ResearchStudy researchStudyR5 = jsonParserR5.parseResource(ResearchStudy.class,
                r5_input);

        org.hl7.fhir.r4.model.ResearchStudy researchStudyR4 = converter.convertR5ToR4(researchStudyR5);

        Response responseCreate = RestAssured
                .given()
                .contentType(Constants.CT_FHIR_JSON_NEW)
                .body(jsonParserR4.encodeResourceToString(researchStudyR4))
                .when()
                .post(getFhirUrl() + "/ResearchStudy")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        org.hl7.fhir.r4.model.ResearchStudy researchStudyRes = this.fhirClientR4
                .parseResource(org.hl7.fhir.r4.model.ResearchStudy.class, responseCreate.asString());
        Assertions.assertNotNull(researchStudyRes);

        // Converte da R4 a R5
        ResearchStudy convertedBackR5 = converter.convertR4ToR5(researchStudyR4);
        Assertions.assertNotNull(convertedBackR5);

        assertEquals(jsonParserR5.encodeResourceToString(convertedBackR5),
                jsonParserR5.encodeResourceToString(researchStudyR5),
                "Risorse uguali");

    }

    @Test
    void saveGetConvertSimpleResearchStudyWithPractitionerFhir() {
        InputStream r5_input_practitioner = this.getClass().getResourceAsStream("/practitioner_50_example.json");
        if (r5_input_practitioner == null) {
            log.error("Failed to load resource: practitioner_50_example.json");
            throw new IllegalStateException("Resource not found");
        }

        Practitioner practitionerR5 = jsonParserR5.parseResource(Practitioner.class,
                r5_input_practitioner);

        org.hl7.fhir.r4.model.Practitioner practitionerR4 = (org.hl7.fhir.r4.model.Practitioner) VersionConvertorFactory_40_50
                .convertResource(practitionerR5);

        Response responseCreatePractitioner = RestAssured
                .given()
                .contentType(Constants.CT_FHIR_JSON_NEW)
                .body(jsonParserR4.encodeResourceToString(practitionerR4))
                .when()
                .post(getFhirUrl() + "/Practitioner")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        org.hl7.fhir.r4.model.Practitioner practitionerRes = this.fhirClientR4
                .parseResource(org.hl7.fhir.r4.model.Practitioner.class, responseCreatePractitioner.asString());
        Assertions.assertNotNull(practitionerRes);

        // Converte da R4 a R5
        Practitioner convertedBackR5Practitioner = (Practitioner) VersionConvertorFactory_40_50
                .convertResource(practitionerRes);
        Assertions.assertNotNull(convertedBackR5Practitioner);

        // setto id e meta che non ci sono nel file di partenza json da cui parte la
        // risorsa che verrà salvata sul fhir
        practitionerR5.setId(convertedBackR5Practitioner.getId());
        practitionerR5.setMeta(convertedBackR5Practitioner.getMeta());

        assertEquals(jsonParserR5.encodeResourceToString(convertedBackR5Practitioner),
                jsonParserR5.encodeResourceToString(practitionerR5),
                "Risorse uguali");

        /* ---------------------------- RS ------------------------------- */
        InputStream r5_input = this.getClass().getResourceAsStream("/researchstudy_50_example_with_practitioner.json");
        if (r5_input == null) {
            log.error("Failed to load resource: researchstudy_50_example.json");
            throw new IllegalStateException("Resource not found");
        }

        ResearchStudy researchStudyR5 = jsonParserR5.parseResource(ResearchStudy.class,
                r5_input);

        // faccio override del valore presente nel json di test, perchè l'ho salvato nel
        // passo precedente e recuperato dal test container fhir l'id del practitioner
        researchStudyR5.getAssociatedParty().get(0).getParty().setReference(practitionerR5.getId());

        org.hl7.fhir.r4.model.ResearchStudy researchStudyR4 = converter.convertR5ToR4(researchStudyR5);

        Response responseCreateResearchStudy = RestAssured
                .given()
                .contentType(Constants.CT_FHIR_JSON_NEW)
                .body(jsonParserR4.encodeResourceToString(researchStudyR4))
                .when()
                .post(getFhirUrl() + "/ResearchStudy")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        org.hl7.fhir.r4.model.ResearchStudy researchStudyRes = this.fhirClientR4
                .parseResource(org.hl7.fhir.r4.model.ResearchStudy.class, responseCreateResearchStudy.asString());
        Assertions.assertNotNull(researchStudyRes);

        // Converte da R4 a R5
        ResearchStudy convertedBackR5 = converter.convertR4ToR5(researchStudyR4);
        Assertions.assertNotNull(convertedBackR5);

        assertEquals(jsonParserR5.encodeResourceToString(convertedBackR5),
                jsonParserR5.encodeResourceToString(researchStudyR5),
                "Risorse uguali");

    }
}
