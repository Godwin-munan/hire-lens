package com.munan.gateway.service.impl.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.munan.gateway.domain.document.RequestMetadata;
import com.munan.gateway.repository.RequestMetadataRepository;
import com.munan.gateway.repository.SkillRepository;
import com.munan.gateway.service.languageModel.NameLangService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class DocumentParserServiceImplTest {

    private DocumentParserServiceImpl documentParserService;

    @Autowired
    RequestMetadataRepository requestMetadataRepository;

    @Autowired
    SkillRepository skillRepository;

    @BeforeEach
    void setup() {
        // Create a mock for the dependency
        NameLangService nameLangService = mock(NameLangService.class);

        // Stub the behavior of extractNames to return a dummy name
        when(nameLangService.extractNames(anyString(), eq("PERSON"))).thenReturn("Godwin");

        // Stub SKILLS to return a comma-separated string; this will later be split and trimmed.
        when(nameLangService.extractNames(anyString(), eq("SKILLS"))).thenReturn("Java,Spring,Hibernate");

        // Create the service under test
        documentParserService = new DocumentParserServiceImpl(nameLangService, requestMetadataRepository, skillRepository);
    }

    @Test
    void testParseDoc() throws Exception {
        // Prepare a dummy file content that Tika will parse.
        String dummyContent =
            "Godwin is Awesome, and he has skills in Java, Spring, and Hibernate. This is some dummy text ● with special characters ? and #.";

        InputStream is = new ByteArrayInputStream(dummyContent.getBytes());

        // Create a mock MultipartFile that returns the InputStream
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(is);

        RequestMetadata requestMetadata = new RequestMetadata();

        // Call the public parseDoc() method which will use the private methods internally.
        Map<String, Object> result = documentParserService.parseDoc(file, requestMetadata);

        // Assert that the returned map contains the expected key ("PERSON") with the stubbed name from NameLangService.
        assertNotNull(result.get("PERSON"));
        assertEquals("Godwin", result.get("PERSON"));

        // Assert that the SKILLS value is as expected.
        List<String> expectedSkills = List.of("Java", "Spring", "Hibernate");
        assertNotNull(result.get("SKILLS"));
        assertEquals(expectedSkills, result.get("SKILLS"));
    }
}
