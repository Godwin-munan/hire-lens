package com.munan.hirelens.service.languageModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

@Service
public class NameLangService {

    //private static final TokenNameFinderModel MODEL;
    private static final TokenNameFinderModel NAME_FINDER_MODEL;
    private static final TokenizerModel TOKENIZER_MODEL;

    static {
        try (
            InputStream nameFinderModelStream = NameLangService.class.getResourceAsStream("/models/en-ner-person.bin");
            InputStream tokenizerModelStream = NameLangService.class.getResourceAsStream("/models/en-nr-lang-tokens.bin")
        ) {
            if (nameFinderModelStream == null) {
                throw new RuntimeException("Name Finder model file not found in classpath: /models/en-ner-person.bin");
            }
            if (tokenizerModelStream == null) {
                throw new RuntimeException("Tokenizer model file not found in classpath: /models/en-ner-person.bin");
            }

            NAME_FINDER_MODEL = new TokenNameFinderModel(nameFinderModelStream);
            TOKENIZER_MODEL = new TokenizerModel(tokenizerModelStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load models", e);
        }
    }

    public String extractNames(String text) {
        TokenizerME tokenizer = new TokenizerME(TOKENIZER_MODEL);
        String[] tokens = tokenizer.tokenize(text);

        NameFinderME nameFinder = new NameFinderME(NAME_FINDER_MODEL);
        Span[] nameSpans = nameFinder.find(tokens);

        return Arrays.stream(nameSpans)
            .map(span -> String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd())))
            .collect(Collectors.joining(", "));
    }
}
