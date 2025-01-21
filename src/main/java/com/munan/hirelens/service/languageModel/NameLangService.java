package com.munan.hirelens.service.languageModel;

import com.munan.hirelens.enums.ModelTypes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NameLangService {

    //private static final TokenNameFinderModel MODEL;
    private final ModelTrainerService NAME_FINDER_MODEL;
    private static final TokenizerModel TOKENIZER_MODEL;

    static {
        try (InputStream tokenizerModelStream = NameLangService.class.getResourceAsStream("/models/en-nr-lang-tokens.bin")) {
            //            if (nameFinderModelStream == null) {
            //                throw new RuntimeException("Name Finder model file not found in classpath: /models/en-ner-person.bin");
            //            }
            if (tokenizerModelStream == null) {
                throw new RuntimeException("Tokenizer model file not found in classpath: /models/en-nr-lang-tokens.bin");
            }

            //NAME_FINDER_MODEL = new TokenNameFinderModel(nameFinderModelStream);
            TOKENIZER_MODEL = new TokenizerModel(tokenizerModelStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load models", e);
        }
    }

    public String extractNames(String text) {
        //TokenizerME tokenizer = new TokenizerME(TOKENIZER_MODEL);
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        // Define regex patterns for names, emails, URLs, and phone numbers
        //        Pattern[] patterns = new Pattern[] {
        //            // Names
        //            Pattern.compile("\\b[A-Z][a-z]+\\s[A-Z][a-z]+\\b"), // Full name
        //            // Emails
        //            Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
        //            // URLs
        //            Pattern.compile("https?://(?:www\\.)?[a-zA-Z0-9./\\-_]+"),
        //            // Phone numbers
        //            Pattern.compile("\\+?[0-9\\-\\s()]{7,15}")
        //        };
        //
        //        RegexNameFinder regexFinder = new RegexNameFinder(patterns, text);

        System.out.println("TEXT : " + text);

        NameFinderME nameFinder = new NameFinderME(NAME_FINDER_MODEL.getModelMap().get(ModelTypes.PERSON));
        Span[] nameSpans = nameFinder.find(tokens);

        return Arrays.stream(nameSpans)
            .filter(span -> "person".equalsIgnoreCase(span.getType()))
            //.map(span -> {
            // Retrieve tokens corresponding to this span
            //String entity = String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd()));
            // Add tags around the entity
            //return "<START:" + span.getType().toUpperCase() + "> " + entity + " <END>";
            //return entity;
            //return span;
            //}) // Add a check for the type of the span
            //.map(span -> String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd())))
            .map(span -> {
                //nameFinder.clearAdaptiveData();
                // Retrieve tokens corresponding to this span and return them as a single string
                String entity = String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd()));

                //String[] subArray = Arrays.copyOfRange(tokens, span.getStart(), span.getEnd());
                //String entity = (subArray.length > 0) ? subArray[0] : "";

                System.out.println("#########################################Span Type : " + span.getType());
                //System.out.println("#########################################Extracted Entity: " + entity); // Print the entity for debugging
                return entity;
            })
            .collect(Collectors.joining(", "));
    }
}
