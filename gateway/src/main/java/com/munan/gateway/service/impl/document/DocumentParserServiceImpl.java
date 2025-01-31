package com.munan.gateway.service.impl.document;

import com.munan.gateway.service.document.DocumentParserService;
import com.munan.gateway.service.languageModel.NameLangService;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
//@RequiredArgsConstructor
public class DocumentParserServiceImpl implements DocumentParserService {

    private final NameLangService nameLangService;

    public DocumentParserServiceImpl(NameLangService nameLangService) {
        this.nameLangService = nameLangService;
    }

    @Override
    public Map<String, Object> parseDoc(MultipartFile file) throws IOException, TikaException {
        // Step 1: Extract text from file
        String text = extractTextFromFile(file);

        // Step 2: Parse details using regex or NLP
        return extractDetailsFromText(text);
    }

    private String extractTextFromFile(MultipartFile file) throws IOException, TikaException {
        try (InputStream inputStream = file.getInputStream()) {
            Tika tika = new Tika();

            //String detectedType = tika.detect(inputStream);
            //Translator translator = tika.getTranslator();
            //translator.translate()
            String rawText = tika.parseToString(inputStream);

            //for (char c : rawText.toCharArray()) {
            //    System.out.println("Char: " + c + " | Unicode: " + (int) c);
            //}

            return rawText
                .replaceAll("(?m)^[\\s]*\n", "")
                .replaceAll("[?$#*]+", "") // Remove ?, $, #, *, and :
                .replaceAll("[\u2022\u00B7\u2013]", "-") // Replace bullet (\u2022), middle dot (\u00B7), and en dash (\u2013) with a hyphen (-)
                .trim(); // Trim leading and trailing spaces
        }
        //Note: Error is handled in calling function
    }

    private Map<String, Object> extractDetailsFromText(String text) throws IOException {
        Map<String, Object> details = new HashMap<>();

        // Example regex for basic data extraction

        //        Matcher emailMatcher = RegexUtils.emailGroup(text);
        //        if (emailMatcher.find()) {
        //            details.put("email", emailMatcher.group());
        //        }
        //
        //        Matcher phoneMatcher = RegexUtils.phoneContactGroup(text);
        //        if (phoneMatcher.find()) {
        //            details.put("phone", phoneMatcher.group());
        //        }

        String extractedNames = nameLangService.extractNames(text);

        // Return results as a map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("PERSON", extractedNames);

        // Add more regex or NLP for names, skills, etc.

        return resultMap;
    }
}
