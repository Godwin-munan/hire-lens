package com.munan.gateway.service.impl.document;

import static com.munan.gateway.utils.Util.PERSON_LABEL;
import static com.munan.gateway.utils.Util.SKILLS_LABEL;

import com.munan.gateway.domain.document.ParsedDocument;
import com.munan.gateway.domain.document.RequestMetadata;
import com.munan.gateway.domain.document.Skill;
import com.munan.gateway.enums.ModelTypes;
import com.munan.gateway.enums.ParseStatus;
import com.munan.gateway.repository.RequestMetadataRepository;
import com.munan.gateway.repository.SkillRepository;
import com.munan.gateway.service.document.DocumentParserService;
import com.munan.gateway.service.languageModel.NameLangService;
import com.munan.gateway.utils.Util;
import com.munan.gateway.utils.regex.RegexUtils;
import jakarta.persistence.Column;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentParserServiceImpl implements DocumentParserService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserServiceImpl.class);
    private final NameLangService nameLangService;
    private final RequestMetadataRepository requestMetadataRepository;
    private final SkillRepository skillRepository;

    public DocumentParserServiceImpl(
        NameLangService nameLangService,
        RequestMetadataRepository requestMetadataRepository,
        SkillRepository skillRepository
    ) {
        this.nameLangService = nameLangService;
        this.requestMetadataRepository = requestMetadataRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public Map<String, Object> parseDoc(MultipartFile file, RequestMetadata requestMetadata) throws IOException, TikaException {
        // Step 1: Extract text from file
        String text = extractTextFromFile(file);

        ParsedDocument parsedDocument = new ParsedDocument();

        Instant start = Instant.now();

        Map<String, Object> extractDetailsFromText = extractDetailsFromText(text);

        Instant end = Instant.now();
        Long parseDuration = Duration.between(start, end).toSeconds();

        if (!extractDetailsFromText.isEmpty()) {
            Matcher emailMatcher = RegexUtils.emailGroup(text);
            if (emailMatcher.find()) {
                parsedDocument.setEmail(emailMatcher.group().trim().toLowerCase());
            }

            Matcher phoneMatcher = RegexUtils.phoneContactGroup(text);
            if (phoneMatcher.find()) {
                parsedDocument.setPhone(phoneMatcher.group().trim());
            }

            parsedDocument.setParseDuration(parseDuration + " seconds");
            parsedDocument.setParseTimestamp(start);
            parsedDocument.setFileExtension(Util.getFileExtension(file));
            parsedDocument.setFileName(Util.getNewFileName(file));
            parsedDocument.setFileSize(Util.getFileSizeInKB(file) + "KB");

            String applicantName = Objects.nonNull(extractDetailsFromText.get(PERSON_LABEL))
                ? (String) extractDetailsFromText.get(PERSON_LABEL)
                : "Unnamed Applicant";
            parsedDocument.setApplicantName(applicantName);

            Set<Skill> skills = Optional.ofNullable(extractDetailsFromText.get(SKILLS_LABEL))
                .filter(List.class::isInstance)
                .map(list -> (List<String>) list)
                .map(skillStrings ->
                    skillStrings
                        .stream()
                        .map(String::trim)
                        .distinct()
                        .filter(s -> !s.isEmpty())
                        .map(skillName -> {
                            // Lookup the existing skill
                            return skillRepository
                                .findByName(skillName)
                                .orElseGet(() -> {
                                    Skill newSkill = new Skill();
                                    newSkill.setName(skillName);
                                    return skillRepository.save(newSkill);
                                });
                        })
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet());
            parsedDocument.setSkills(skills);

            boolean personStatus =
                Objects.nonNull(extractDetailsFromText.get(PERSON_LABEL)) &&
                !"".equalsIgnoreCase(((String) extractDetailsFromText.get(PERSON_LABEL)));
            boolean skillStatus =
                Objects.nonNull(extractDetailsFromText.get(SKILLS_LABEL)) && !((String) extractDetailsFromText.get(PERSON_LABEL)).isEmpty();

            if (personStatus && skillStatus) {
                parsedDocument.setParseStatus(ParseStatus.SUCCESS);
            } else if (personStatus || skillStatus) {
                parsedDocument.setParseStatus(ParseStatus.PARTIAL);
            } else {
                parsedDocument.setParseStatus(ParseStatus.FAILED);
            }

            requestMetadata.setParsedDocument(parsedDocument);
            requestMetadataRepository.save(requestMetadata);
        }

        // Step 2: Parse details using regex or NLP
        return extractDetailsFromText;
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
    }

    public Map<String, Object> extractDetailsFromText(String text) throws IOException {
        String extractedPersonName = nameLangService.extractNames(text, PERSON_LABEL);
        List<String> extractedSkills = List.of(nameLangService.extractNames(text, SKILLS_LABEL).trim().split(","));

        // Return results as a map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(PERSON_LABEL, extractedPersonName);
        log.info("#####################CUSTOM-LOG : Extracted {} name from uploaded document", extractedPersonName);
        resultMap.put(SKILLS_LABEL, extractedSkills);
        log.info("#####################CUSTOM-LOG : Extracted {} skills from uploaded document", extractedSkills);

        // Add more regex or NLP for names, skills, etc.
        return resultMap;
    }
}
