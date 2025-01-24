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

    //private static final TokenizerModel TOKENIZER_MODEL;

    static {
        try (InputStream tokenizerModelStream = NameLangService.class.getResourceAsStream("/models/en-nr-lang-tokens.bin")) {
            if (tokenizerModelStream == null) {
                throw new RuntimeException("Tokenizer model file not found in classpath: /models/en-nr-lang-tokens.bin");
            }
            //NAME_FINDER_MODEL = new TokenNameFinderModel(nameFinderModelStream);
            //TOKENIZER_MODEL = new TokenizerModel(tokenizerModelStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load models", e);
        }
    }

    public String extractNames(String text) {
        //TokenizerME tokenizer = new TokenizerME(TOKENIZER_MODEL);
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        System.out.println("TEXT : " + text);

        NameFinderME nameFinder = new NameFinderME(NAME_FINDER_MODEL.getModelMap().get(ModelTypes.PERSON));
        Span[] nameSpans = nameFinder.find(tokens);

        return Arrays.stream(nameSpans)
            .filter(span -> "person".equalsIgnoreCase(span.getType()))
            .map(span -> {
                String[] words = Arrays.copyOfRange(tokens, span.getStart(), span.getEnd());
                //System.out.println("Words : " + Arrays.toString(words));
                return String.join(" ", Arrays.copyOfRange(words, 0, Math.min(words.length, 3)));
            })
            .collect(Collectors.joining(", "))
            .split(",")[0];
    }
}
