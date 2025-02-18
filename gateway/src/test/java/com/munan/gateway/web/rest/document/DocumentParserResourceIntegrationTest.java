package com.munan.gateway.web.rest.document;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.munan.gateway.service.impl.document.DocumentParserServiceImpl;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class DocumentParserResourceIntegrationTest {

    @MockitoSpyBean
    private DocumentParserServiceImpl documentParserService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {}

    @Test
    void testUploadFile_success() throws Exception {
        // Prepare dummy file content containing details.
        String dummyContent =
            """
            Godwin Ngusen Jethro
            Contact: godwin@example.com, Phone: 123-456-7890, Skills: Java, Spring, Hibernate
            """;

        MockMultipartFile file = new MockMultipartFile("file", "dummy.pdf", "application/pdf", dummyContent.getBytes());

        // Create a spy of your service
        //DocumentParserServiceImpl spyService = Mockito.spy(documentParserService);

        // Prepare a stubbed result map
        Map<String, Object> stubbedResult = new HashMap<>();
        stubbedResult.put("PERSON", "Godwin Ngusen Jethro");
        stubbedResult.put("SKILLS", List.of("Java", "Spring", "Hibernate"));

        //ReflectionTestUtils

        // Stub the method
        doReturn(stubbedResult).when(documentParserService).extractDetailsFromText(Mockito.anyString());

        // Perform the file upload request.
        mockMvc
            .perform(multipart("/api/doc/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.PERSON").value("Godwin Ngusen Jethro"))
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
