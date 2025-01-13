package com.munan.hirelens.service.impl.document;

import com.munan.hirelens.service.document.DocumentParserService;
import com.munan.hirelens.service.languageModel.NameLangService;
import com.munan.hirelens.utils.regex.RegexUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DocumentParserServiceImpl implements DocumentParserService {

    private final NameLangService nameLangService;

    @Override
    public Map<String, Object> parseDoc(MultipartFile file) throws IOException, TikaException {
        // Step 1: Extract text from file
        String text = extractTextFromFile(file);
        //System.out.println("################################File String : " + text);

        // Step 2: Parse details using regex or NLP

        return extractDetailsFromText(text);
    }

    private String extractTextFromFile(MultipartFile file) throws IOException, TikaException {
        try (InputStream inputStream = file.getInputStream()) {
            Tika tika = new Tika();
            //System.out.println("################################File BYTES : " + Arrays.toString(inputStream.readAllBytes()));

            String detectedType = tika.detect(inputStream);

            return tika.parseToString(inputStream);
        }
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
