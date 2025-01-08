package com.munan.hirelens.service.languageModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

@Service
public class NameLangService {
    //    private static final TokenNameFinderModel MODEL;

    //    static {
    //        try (InputStream modelStream = NameLangService.class.getResourceAsStream("/en-ner-person.bin")) {
    //            MODEL = new TokenNameFinderModel(modelStream);
    //        } catch (IOException e) {
    //            throw new RuntimeException("Failed to load name finder model", e);
    //        }
    //    }

    //    public String extractNames(String text) {
    //        NameFinderME nameFinder = new NameFinderME(MODEL);
    //        String[] tokens = text.split("\\s+"); // Replace with TokenizerME for better accuracy
    //        Span[] nameSpans = nameFinder.find(tokens);
    //
    //        return Arrays.stream(nameSpans)
    //            .map(span -> String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd())))
    //            .collect(Collectors.joining(", "));
    //    }
}
