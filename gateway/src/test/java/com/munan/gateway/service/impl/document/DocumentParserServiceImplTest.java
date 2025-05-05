package com.munan.gateway.service.impl.document;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.munan.gateway.domain.document.ParsedDocument;
import com.munan.gateway.domain.document.RequestMetadata;
import com.munan.gateway.domain.document.Skill;
import com.munan.gateway.repository.RequestMetadataRepository;
import com.munan.gateway.repository.SkillRepository;
import com.munan.gateway.service.ExperienceService;
import com.munan.gateway.service.languageModel.NameLangService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class DocumentParserServiceImplTest {

    private DocumentParserServiceImpl documentParserService;

    @Mock
    private RequestMetadataRepository requestMetadataRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ExperienceService experienceService;

    @Mock
    private NameLangService nameLangService;

    @BeforeEach
    void setup() {
        documentParserService = new DocumentParserServiceImpl(
            nameLangService,
            requestMetadataRepository,
            skillRepository,
            experienceService
        );
    }

    @Test
    void testParseDoc_withDetails() throws Exception {
        // Prepare dummy file content including a name, email, phone, and skills.
        String dummyContent = "Name: Godwin, Contact: godwin@example.com, Phone: 123-456-7890, Skills: Java, Spring, Hibernate";

        // Use Spring's MockMultipartFile to guarantee a non-null original filename.
        MultipartFile file = new MockMultipartFile(
            "file", // form field name
            "dummy.pdf", // original filename
            "application/pdf", // content type
            dummyContent.getBytes() // file content
        );

        // Stub repository behavior: assume no Skill exists yet.
        when(nameLangService.extractNames(anyString(), anyString())).thenAnswer(invocation -> {
            String type = invocation.getArgument(1);
            if ("PERSON".equalsIgnoreCase(type)) {
                return "Godwin";
            } else if ("SKILLS".equalsIgnoreCase(type)) {
                return "Java, Spring, Hibernate";
            }
            return "";
        });

        RequestMetadata requestMetadata = new RequestMetadata();

        // Invoke the public parseDoc() method.
        Map<String, Object> result = documentParserService.parseDoc(file, requestMetadata);

        // Validate that a ParsedDocument was created and linked to requestMetadata.
        ParsedDocument parsedDocument = requestMetadata.getParsedDocument();
        assertNotNull(parsedDocument, "ParsedDocument should not be null");
        // Now, since dummyContent includes "person: Godwin", your extraction logic should find it.
        //assertEquals("Godwin", parsedDocument.getApplicantName(), "Applicant name should be 'Godwin'");

        // Assuming RegexUtils extracts email and phone correctly.
        assertEquals("godwin@example.com", parsedDocument.getEmail(), "Email should be 'godwin@example.com'");
        assertEquals("123-456-7890", parsedDocument.getPhone(), "Phone should be '123-456-7890'");

        // Validate that skills were set.
        Set<Skill> skills = parsedDocument.getSkills();
        assertNotNull(skills, "Skills set should not be null");

        /*
        assertEquals(3, skills.size(), "There should be 3 skills");
        assertTrue(skills.stream().anyMatch(s -> "Java".equals(s.getName())), "Skill 'Java' should be present");
        assertTrue(skills.stream().anyMatch(s -> "Spring".equals(s.getName())), "Skill 'Spring' should be present");
        assertTrue(skills.stream().anyMatch(s -> "Hibernate".equals(s.getName())), "Skill 'Hibernate' should be present");
         */

        // Verify that the top-level repository save was invoked.
        verify(requestMetadataRepository).save(requestMetadata);
    }

    @Test
    void testParseDoc_emptyDetails() throws Exception {
        // Prepare dummy file content with no extractable details.
        String dummyContent = "Some content with no extractable details.";

        MultipartFile file = new MockMultipartFile(
            "file", // form field name
            "dummy.pdf", // original filename
            "text/plain", // content type
            dummyContent.getBytes() // file content
        );

        // Stub NameLangService to return empty strings for both PERSON and SKILLS.
        when(nameLangService.extractNames(anyString(), eq("PERSON"))).thenReturn("");
        when(nameLangService.extractNames(anyString(), eq("SKILLS"))).thenReturn("");

        RequestMetadata requestMetadata = new RequestMetadata();

        Map<String, Object> result = documentParserService.parseDoc(file, requestMetadata);
        // When details are empty, the if-block should be skipped.
        //assertEquals(Collections.emptyMap(), result);
        //assertNull(requestMetadata.getParsedDocument());
    }
}
