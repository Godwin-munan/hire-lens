package com.munan.gateway.cucumber.stepdefs;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DocumentParserResourceDefs extends StepDefs {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserResourceDefs.class);

    private ResponseEntity<String> response;
    private final RestTemplate restTemplate = new RestTemplate();
    private File fileToUpload;

    @Given("I have a valid {string} file for person extraction")
    public void iHaveAValidFileForPersonExtraction(String filename) throws Exception {
        fileToUpload = new File("src/test/resources/document/" + filename);
        Assertions.assertTrue(fileToUpload.exists(), "File should exist for test.");
    }

    @Given("I have a valid {string} file for skill extraction")
    public void iHaveAValidFileForSkillsExtraction(String filename) throws Exception {
        fileToUpload = new File("src/test/resources/document/" + filename);
        Assertions.assertTrue(fileToUpload.exists(), "File should exist for test.");
    }

    @Given("I have an invalid {string} file")
    public void iHaveAnInvalidFile(String filename) throws Exception {
        fileToUpload = new File("src/test/resources/document/" + filename);
        Assertions.assertTrue(fileToUpload.exists(), "File should exist for test.");
    }

    @Given("I have an empty file")
    public void iHaveAnEmptyFile() throws Exception {
        fileToUpload = File.createTempFile("empty", ".pdf");
        Assertions.assertTrue(fileToUpload.exists(), "Empty file should exist for test.");
    }

    @When("I upload the file to {string}")
    public void iUploadTheFileTo(String endpoint) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(fileToUpload));

        // Disable the default error handling to capture the response even if it's an error
        /*
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
         */

        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String BASE_URL = "http://localhost:10344";
            response = restTemplate.exchange(BASE_URL + endpoint, HttpMethod.POST, requestEntity, String.class);
            //response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException.BadRequest ex) {
            log.info(ex.getMessage());
            response = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }
    }

    @Then("the response should contain extracted person {string}")
    public void theResponseShouldContainExtractedPersonData(String expectedContent) throws Exception {
        Assertions.assertNotNull(response, "Response should not be null.");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected HTTP 200 OK");
        Assertions.assertTrue(
            Objects.requireNonNull(response.getBody()).contains(expectedContent),
            "Response should contain extracted data."
        );
    }

    @Then("the response should contain extracted skills {string}")
    public void theResponseShouldContainExtractedSkillData(String expectedContent) throws Exception {
        Assertions.assertNotNull(response, "Response should not be null.");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected HTTP 200 OK");
        Assertions.assertTrue(
            Objects.requireNonNull(response.getBody()).contains(expectedContent),
            "Response should contain extracted data."
        );
    }

    @Then("the response should contain an error message {string}")
    public void theResponseShouldContainAnErrorMessage(String expectedMessage) throws Exception {
        Assertions.assertNotNull(response, "Response should not be null.");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected HTTP 400 Bad Request");
        Assertions.assertTrue(
            Objects.requireNonNull(response.getBody()).contains(expectedMessage),
            "Response should contain an error message."
        );
        //actions.andExpect(jsonPath("$.error_description").value(expectedMessage));
    }
}
