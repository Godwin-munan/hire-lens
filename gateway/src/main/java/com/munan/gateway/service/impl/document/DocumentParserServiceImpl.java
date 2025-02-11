package com.munan.gateway.service.impl.document;

import static com.munan.gateway.utils.Util.PERSON_LABEL;
import static com.munan.gateway.utils.Util.SKILLS_LABEL;

import com.munan.gateway.enums.ModelTypes;
import com.munan.gateway.service.document.DocumentParserService;
import com.munan.gateway.service.languageModel.NameLangService;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
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

            //            for (char c : rawText.toCharArray()) {
            //                System.out.println("Char: " + c + " | Unicode: " + (int) c);
            //            }

            return rawText
                .replaceAll("(?m)^[\\s]*\n", "")
                .replaceAll("[?$#*]+", "") // Remove ?, $, #, *, and :
                .replaceAll("[\u2022\u00B7\u2013\u25CF]", "-") // Replace bullet (\u2022), middle dot (\u00B7), en dash (\u2013), black circle (\u25CF) with a hyphen (-)
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

        String extractedPersonName = nameLangService.extractNames(text, PERSON_LABEL);
        List<String> extractedSkills = List.of(nameLangService.extractNames(text, SKILLS_LABEL).trim().split(","));

        // Return results as a map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(PERSON_LABEL, extractedPersonName);
        resultMap.put(SKILLS_LABEL, extractedSkills);

        // Add more regex or NLP for names, skills, etc.
        return resultMap;
    }
}
