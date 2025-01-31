package com.munan.gateway.service.languageModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

@Service
public class FileSanitizationService {

    //private static final String PERSON_TAG_REGEX = "<START:person>.*?<END>";
    private static final String PERSON_TAG_REGEX = "([A-Za-z]+(?:\\s[A-Za-z]+)*|[.])\\s*\\[(person|O)\\]";
    private static final Pattern PERSON_TAG_PATTERN = Pattern.compile(PERSON_TAG_REGEX);

    // Paths relative to the resources directory
    private static final String INPUT_FILE_PATH = "/data/from.sanitize.txt";
    private static final String OUTPUT_FILE_PATH = "/data/destination.sanitize.one.txt";

    public void sanitizeFile() throws IOException, TikaException {
        // Read the file content using getResourceAsStream
        InputStream inputStream = NameLangService.class.getResourceAsStream(INPUT_FILE_PATH);
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found at: " + INPUT_FILE_PATH);
        }

        Tika tika = new Tika();
        String fileContent = tika.parseToString(inputStream);

        // Extract and sanitize text
        String sanitizedContent = extractPersonTags(fileContent);

        // Write sanitized content back to the resources directory
        Path outputPath = Paths.get("src/main/resources" + OUTPUT_FILE_PATH);
        Files.createDirectories(outputPath.getParent()); // Ensure the directories exist
        Files.write(outputPath, sanitizedContent.getBytes());
    }

    private String extractPersonTags(String content) {
        StringBuilder sanitizedContent = new StringBuilder();
        Matcher matcher = PERSON_TAG_PATTERN.matcher(content);

        while (matcher.find()) {
            sanitizedContent.append(matcher.group()).append(System.lineSeparator());
        }
        return sanitizedContent.toString();
    }

    //    private static final String PERSON_TAG_REGEX = "<START:person>.*?<END>";
    //    private static final Pattern PERSON_TAG_PATTERN = Pattern.compile(PERSON_TAG_REGEX);
    //
    //    // Paths relative to the resources directory
    //    //destination.sanitize.one.txt
    //    private static final String INPUT_FILE_PATH = "/data/from.sanitize.txt";
    //    private static final String OUTPUT_FILE_PATH = "/data/destination.sanitize.one.txt";
    //    private int lineCounter = 0;
    //
    //    public String sanitizeFile() throws IOException, TikaException {
    //        // Read the file content using getResourceAsStream
    //        InputStream inputStream = NameLangService.class.getResourceAsStream(INPUT_FILE_PATH);
    //        if (inputStream == null) {
    //            throw new FileNotFoundException("Input file not found at: " + INPUT_FILE_PATH);
    //        }
    //
    //        Tika tika = new Tika();
    //        String fileContent = tika.parseToString(inputStream);
    //
    //        // Extract and sanitize text
    //        Map<String, String> sanitizedContent = extractPersonTags(fileContent);
    //
    //        // Write sanitized content back to the resources directory
    //        Path outputPath = Paths.get("src/main/resources" + OUTPUT_FILE_PATH);
    //        Files.createDirectories(outputPath.getParent()); // Ensure the directories exist
    //        Files.write(outputPath, sanitizedContent.get("SANITIZE").getBytes());
    //
    //        return sanitizedContent.get("LINE");
    //    }
    //
    //    private String extractPersonTags(String content) {
    //        Set<String> uniqueTags = new LinkedHashSet<>(); // Use LinkedHashSet to maintain insertion order
    //        Matcher matcher = PERSON_TAG_PATTERN.matcher(content);
    //
    //        while (matcher.find()) {
    //            uniqueTags.add(matcher.group());
    //            lineCounter++;
    //        }
    //
    //        return String.join(System.lineSeparator(), uniqueTags);
    //    }

    public void sanitizeFileV2() throws IOException, TikaException {
        // Read the file content using getResourceAsStream
        InputStream inputStream = NameLangService.class.getResourceAsStream(INPUT_FILE_PATH);
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found at: " + INPUT_FILE_PATH);
        }

        Tika tika = new Tika();
        String fileContent = tika.parseToString(inputStream);

        // Extract and sanitize text
        String sanitizedContent = extractPersonTags(fileContent);

        // Remove empty lines from the sanitized content
        String sanitizedContentWithoutEmptyLines = removeEmptyLines(sanitizedContent);

        // Write sanitized content back to the resources directory
        Path outputPath = Paths.get("src/main/resources" + OUTPUT_FILE_PATH);
        Files.createDirectories(outputPath.getParent()); // Ensure the directories exist
        Files.write(outputPath, sanitizedContentWithoutEmptyLines.getBytes());
    }

    private String extractPersonTagsV2(String content) {
        StringBuilder sanitizedContent = new StringBuilder();
        Matcher matcher = PERSON_TAG_PATTERN.matcher(content);

        while (matcher.find()) {
            sanitizedContent.append(matcher.group()).append(System.lineSeparator());
        }
        return sanitizedContent.toString();
    }

    private String removeEmptyLines(String content) {
        // Split the content by lines, filter out empty lines, and then join them back
        return Arrays.stream(content.split(System.lineSeparator()))
            .filter(line -> !line.trim().isEmpty()) // Remove empty lines
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
