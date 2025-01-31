package com.munan.gateway.web.rest.document;

import com.munan.gateway.service.document.DocumentParserService;
import com.munan.gateway.service.languageModel.FileSanitizationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
//@RequiredArgsConstructor
public class DocumentParserResource {

    private final DocumentParserService docParserService;
    private final FileSanitizationService fileSanitizationService;

    public DocumentParserResource(DocumentParserService docParserService, FileSanitizationService fileSanitizationService) {
        this.docParserService = docParserService;
        this.fileSanitizationService = fileSanitizationService;
    }

    //public DocumentParserResource(DocumentParserService docParserService) {
    //        this.docParserService = docParserService;
    //    }

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

    @GetMapping("/sanitize")
    public String sanitizeFile() {
        //        try {
        //            fileSanitizationService.sanitizeFile();
        //            return "File sanitized successfully! Check the output at /data/destination.sanitize.txt";
        //        } catch (IOException | TikaException e) {
        //            e.printStackTrace();
        //            return "Failed to sanitize file: " + e.getMessage();
        //        }

        return "";
    }
}
