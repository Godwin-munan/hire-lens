package com.munan.gateway.service.languageModel;

import static com.munan.gateway.utils.Util.TOKENIZER_MODEL_URL;

import com.munan.gateway.enums.ModelTypes;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

@Service
public class NameLangService {

    //private static final TokenNameFinderModel MODEL;
    private static final TokenizerModel TOKENIZER_MODEL;
    private final ModelTrainerService NAME_FINDER_MODEL;

    public NameLangService(ModelTrainerService NAME_FINDER_MODEL) {
        this.NAME_FINDER_MODEL = NAME_FINDER_MODEL;
    }

    static {
        try (InputStream tokenizerModelStream = NameLangService.class.getResourceAsStream(TOKENIZER_MODEL_URL)) {
            if (tokenizerModelStream == null) {
                throw new RuntimeException("Tokenizer model file not found in classpath:" + TOKENIZER_MODEL_URL);
            }
            //NAME_FINDER_MODEL = new TokenNameFinderModel(nameFinderModelStream);
            TOKENIZER_MODEL = new TokenizerModel(tokenizerModelStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load models", e);
        }
    }

    public String extractNames(String text, String key) {
        TokenizerME tokenizer = new TokenizerME(TOKENIZER_MODEL);
        String[] tokens = tokenizer.tokenize(text);

        System.out.println("TEXT : " + text);

        NameFinderME nameFinder;
        if (ModelTypes.PERSON.name().equalsIgnoreCase(key)) {
            nameFinder = new NameFinderME(NAME_FINDER_MODEL.getModelMap().get(ModelTypes.PERSON));
        } else {
            nameFinder = new NameFinderME(NAME_FINDER_MODEL.getModelMap().get(ModelTypes.SKILLS));
        }

        Span[] nameSpans = nameFinder.find(tokens);

        //        return Arrays.stream(nameSpans)
        //            .filter(span -> key.equalsIgnoreCase(span.getType()))
        //            .map(span -> {
        //                String[] words = Arrays.copyOfRange(tokens, span.getStart(), span.getEnd());
        //                //System.out.println("Words : " + Arrays.toString(words));
        //                return key.equalsIgnoreCase(ModelTypes.PERSON.name()) ?
        //                    String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 3))) :
        //                    words;
        //            })
        //            .collect(Collectors.joining(", "))
        //            .split(",")[0];

        String result = Arrays.stream(nameSpans)
            .filter(span -> key.equalsIgnoreCase(span.getType()))
            .map(span -> {
                String[] words = Arrays.copyOfRange(tokens, span.getStart(), span.getEnd());
                // For PERSON, return a truncated version (first 3 words joined by a space)
                if (key.equalsIgnoreCase(ModelTypes.PERSON.name())) {
                    return String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 3)));
                }
                // For SKILLS, return the full span as a single string (words joined by a space)
                else if (key.equalsIgnoreCase(ModelTypes.SKILLS.name())) {
                    System.out.println("SKILLS SPANS : " + Arrays.toString(words));
                    return String.join(",", words);
                }
                // Default: simply join words with a space
                else {
                    return String.join(" ", words);
                }
            })
            .collect(Collectors.joining(", "));

        // For PERSON, you want to return only the first occurrence;
        // for SKILLS, you want to return all spans (already separated by commas).
        if (key.equalsIgnoreCase(ModelTypes.PERSON.name())) {
            return result.split(",")[0];
        } else {
            return result;
        }
    }
}
