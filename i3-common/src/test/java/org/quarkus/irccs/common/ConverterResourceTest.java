package org.quarkus.irccs.common;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.ResearchStudy;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quarkus.irccs.common.fhir.converters.FHIRResearchStudyConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ConverterResourceTest {
    // Creazione del contesto per FHIR R4
    FhirContext ctx = FhirContext.forR5();

    // Parser JSON di HAPI FHIR
    IParser jsonParser = ctx.newJsonParser();

    FHIRResearchStudyConverter converter = new FHIRResearchStudyConverter(new BaseAdvisor_40_50());

    @Inject
    Logger logger;

    @Test
    void ResearchStudyConverterTest() throws FHIRFormatError, IOException {
        logger.info("ResearchStudyConverterTest");

        InputStream r5_input = this.getClass().getResourceAsStream("/researchstudy_50_example.json");
        if (r5_input == null) {
            logger.error("Failed to load resource: researchstudy_50_example.json");
            throw new IllegalStateException("Resource not found");
        }

        ResearchStudy researchStudyR5 = jsonParser.parseResource(ResearchStudy.class,
                                                                 r5_input);
        assertNotNull(researchStudyR5);

        // Converte da R5 a R4
        org.hl7.fhir.r4.model.ResearchStudy researchStudyR4 = converter.convertR5ToR4(researchStudyR5);
        assertNotNull(researchStudyR4, "Conversione R5 -> R4 fallita");

        // Converte da R4 a R5
        ResearchStudy convertedBackR5 = converter.convertR4ToR5(researchStudyR4);
        assertNotNull(convertedBackR5, "Conversione R4 -> R5 fallita");

        assertEquals(jsonParser.encodeResourceToString(convertedBackR5),
                                jsonParser.encodeResourceToString(researchStudyR5),
                                "Risorse uguali");
    }

    /*
    @BeforeEach
    void setUp() {
        BaseAdvisor_40_50 advisorMock = Mockito.mock(BaseAdvisor_40_50.class);
        converter = new FHIRResearchStudyConverter(advisorMock);
    }*/

    @Test
    void testConvertR4ToR5() {
        org.hl7.fhir.r4.model.ResearchStudy studyR4 = new  org.hl7.fhir.r4.model.ResearchStudy();
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
    void testConvertR5ToR4() {
        org.hl7.fhir.r5.model.ResearchStudy studyR5 = new org.hl7.fhir.r5.model.ResearchStudy();
        studyR5.setId("456");
        studyR5.setTitle("Converted Study");
       // studyR5.setStatus(PublicationStatus.RETIRED);

        org.hl7.fhir.r4.model.ResearchStudy studyR4 = converter.convertR5ToR4(studyR5);

        assertNotNull(studyR4);
        assertEquals("456", studyR4.getId());
        assertEquals("Converted Study", studyR4.getTitle());
        //assertEquals(ResearchStudy.ResearchStudyStatus.WITHDRAWN, studyR4.getStatus());
    }

    @Test
    void testConvertExtensionR4toR5() {
        org.hl7.fhir.r4.model.Extension extR4 = new org.hl7.fhir.r4.model.Extension();
        extR4.setUrl("http://example.com");

        Extension extR5 = converter.convertExtensionR4toR5(extR4);

        assertNotNull(extR5);
        assertEquals("http://example.com", extR5.getUrl());
    }
}
