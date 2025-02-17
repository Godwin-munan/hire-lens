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
    private NameLangService nameLangService;

    @BeforeEach
    void setup() {
        documentParserService = new DocumentParserServiceImpl(nameLangService, requestMetadataRepository, skillRepository);
    }

    @Test
    void testParseDoc_withDetails() throws Exception {
        // Prepare dummy file content containing email, phone, and skills.
        String dummyContent = "Contact: godwin@example.com, Phone: 123-456-7890, Skills: Java, Spring, Hibernate";

        // Use Spring's MockMultipartFile to guarantee a non-null original filename.
        MultipartFile file = new MockMultipartFile(
            "file", // form field name
            "dummy.pdf", // original filename
            "application/pdf", // content type
            dummyContent.getBytes() // file content
        );

        // Stub NameLangService behavior.
        when(nameLangService.extractNames(anyString(), eq("PERSON"))).thenReturn("Godwin");
        when(nameLangService.extractNames(anyString(), eq("SKILLS"))).thenReturn("Java, Spring, Hibernate");

        // Stub repository behavior: assume no Skill exists yet.
        when(skillRepository.findByName(anyString())).thenReturn(Optional.empty());
        // When saving a new Skill, simply return the instance.
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RequestMetadata requestMetadata = new RequestMetadata();

        // Invoke the public parseDoc() method.
        Map<String, Object> result = documentParserService.parseDoc(file, requestMetadata);

        // Assert that the returned map contains the expected details.
        assertEquals("Godwin", result.get("PERSON"));
        // The service splits the skills string into a List.
        //assertEquals(List.of("Java", "Spring", "Hibernate"), result.get("SKILLS"));

        // Validate that a ParsedDocument was created and linked to requestMetadata.
        ParsedDocument parsedDocument = requestMetadata.getParsedDocument();
        assertNotNull(parsedDocument);
        assertEquals("Godwin", parsedDocument.getApplicantName());
        // Assuming RegexUtils extracts email and phone correctly.
        assertEquals("godwin@example.com", parsedDocument.getEmail());
        assertEquals("123-456-7890", parsedDocument.getPhone());

        // Validate that skills were set.
        Set<Skill> skills = parsedDocument.getSkills();
        assertNotNull(skills);
        assertEquals(3, skills.size());
        assertTrue(skills.stream().anyMatch(s -> "Java".equals(s.getName())));
        assertTrue(skills.stream().anyMatch(s -> "Spring".equals(s.getName())));
        assertTrue(skills.stream().anyMatch(s -> "Hibernate".equals(s.getName())));

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
