package com.munan.gateway.web.rest.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.munan.gateway.service.document.DocumentParserService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentParserResource.class)
@AutoConfigureMockMvc(addFilters = false)
public class DocumentParserResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentParserService docParserService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DocumentParserService docParserService() {
            // Create and return a Mockito mock for DocumentParserService
            return org.mockito.Mockito.mock(DocumentParserService.class);
        }
    }

    @Test
    void testUploadFile_success() throws Exception {
        // Prepare dummy file content and create a MockMultipartFile.
        String dummyContent = "Godwin is Awesome, and he has skills in Java, Spring, Hibernate.";
        MockMultipartFile file = new MockMultipartFile("file", "dummy.txt", "text/plain", dummyContent.getBytes());

        // Prepare a dummy result map.
        Map<String, Object> dummyResult = new HashMap<>();
        dummyResult.put("PERSON", "Godwin");
        dummyResult.put("SKILLS", java.util.List.of("Java", "Spring", "Hibernate"));

        // Stub the service method to return our dummy result.
        when(docParserService.parseDoc(any(MockMultipartFile.class), any())).thenReturn(dummyResult);

        // Perform the file upload request.
        mockMvc
            .perform(multipart("/api/doc/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.PERSON").value("Godwin"))
            .andExpect(jsonPath("$.data.SKILLS[0]").value("Java"))
            .andExpect(jsonPath("$.data.SKILLS[1]").value("Spring"))
            .andExpect(jsonPath("$.data.SKILLS[2]").value("Hibernate"));
    }

    @Test
    void testUploadFile_emptyFile() throws Exception {
        // Create an empty file.
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // Perform the file upload request expecting a bad request.
        mockMvc
            .perform(multipart("/api/doc/upload").file(emptyFile))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("400"))
            .andExpect(jsonPath("$.error_description").value("Oops! File is empty, please check."));
    }
}
