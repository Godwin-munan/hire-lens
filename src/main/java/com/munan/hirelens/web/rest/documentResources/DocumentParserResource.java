package com.munan.hirelens.web.rest.documentResources;

import com.munan.hirelens.service.document.DocumentParserService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class DocumentParserResource {

    private final DocumentParserService docParserService;

    public DocumentParserResource(DocumentParserService docParserService) {
        this.docParserService = docParserService;
    }

    @PostMapping("/doc/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty!"));
            }

            // Pass file to service for parsing
            Map<String, Object> parsedData = docParserService.parseDoc(file);

            return ResponseEntity.ok(parsedData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
