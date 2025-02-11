package com.munan.gateway.service.languageModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.munan.gateway.enums.ModelTypes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.MockedConstruction;

public class NameLangServiceTest {

    private NameLangService nameLangService;

    @BeforeEach
    @Timeout(value = 30)
    void setUp() {
        // Create a dummy TokenNameFinderModel (the actual instance is not used because we override NameFinderME.find)
        TokenNameFinderModel dummyModel = mock(TokenNameFinderModel.class);

        // Create a model map and put the dummy model under ModelTypes.PERSON
        Map<ModelTypes, TokenNameFinderModel> modelMap = new HashMap<>();
        modelMap.put(ModelTypes.PERSON, dummyModel);

        // Create a mock ModelTrainerService that returns the dummy model map when getModelMap() is called.
        ModelTrainerService modelTrainerService = mock(ModelTrainerService.class);
        when(modelTrainerService.getModelMap()).thenReturn(modelMap);

        // Create the NameLangService instance using the dummy dependency.
        nameLangService = new NameLangService(modelTrainerService);
    }

    @Test
    @Timeout(value = 30)
    void testExtractNames_returnsExtractedName() throws IOException {
        // Intercept calls to the TokenizerMe and NameFinderME constructors using mockedTokenizer and mockedNameFinder.
        try (
            MockedConstruction<TokenizerME> mockedTokenizer = mockConstruction(TokenizerME.class, (tokenizerMock, context) -> {
                // When tokenize() is called, simulate tokenization.
                // For input "John is awesome", return ["John", "is", "awesome"]
                when(tokenizerMock.tokenize(anyString())).thenReturn(new String[] { "Godwin", "is", "awesome" });
            });
            MockedConstruction<NameFinderME> mockedNameFinder = mockConstruction(NameFinderME.class, (nameFinderMock, context) -> {
                // When find() is called on the constructed NameFinderME, return a Span array with one span.
                // In our example, suppose that for an input text "John is awesome",
                // the SimpleTokenizer tokenizes into ["John", "is", "awesome"]
                // and we want to simulate that a name was found spanning tokens indices 0 (inclusive) to 1 (exclusive).
                when(nameFinderMock.find(any(String[].class))).thenReturn(new Span[] { new Span(0, 1, "person") });
            })
        ) {
            // Given an input text that tokenizes to ["John", "is", "awesome"]
            String inputText = "Godwin is awesome";

            // When
            String extractedName = nameLangService.extractNames(inputText, "PERSON");

            // With the stubs in place:
            // - TokenizerME.tokenize(inputText) returns ["John", "is", "awesome"]
            // - NameFinderME.find(...) returns a Span covering token 0 ("John")
            // So, extractNames() should ultimately return "John".
            assertEquals("Godwin", extractedName);

            // Optionally, you can verify that tokenize() was called with the input text:
            TokenizerME tokenizerInstance = mockedTokenizer.constructed().get(0);
            verify(tokenizerInstance).tokenize(eq(inputText));

            // And that NameFinderME.find was called with the tokenized array:
            NameFinderME nameFinderInstance = mockedNameFinder.constructed().get(0);
            verify(nameFinderInstance).find(eq(new String[] { "Godwin", "is", "awesome" }));
        }
    }
}
