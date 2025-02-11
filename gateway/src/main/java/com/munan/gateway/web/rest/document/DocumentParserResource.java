package com.munan.gateway.web.rest.document;

import com.munan.gateway.service.document.DocumentParserService;
import com.munan.gateway.service.languageModel.FileSanitizationService;
import com.munan.gateway.utils.ApiResponse;
import com.munan.gateway.utils.mapper.ApiMapper;
import com.munan.gateway.web.rest.errors.FileEmptyException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class DocumentParserResource {

    private final DocumentParserService docParserService;

    //private final FileSanitizationService fileSanitizationService;

    public DocumentParserResource(
        DocumentParserService docParserService
        //, FileSanitizationService fileSanitizationService
    ) {
        this.docParserService = docParserService;
        //this.fileSanitizationService = fileSanitizationService;
    }

    @PostMapping("/doc/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file)
        throws TikaException, IOException, FileEmptyException {
        if (file.isEmpty()) {
            throw new FileEmptyException("Oops! File is empty, please check.");
        }

        // Pass file to service for parsing
        Map<String, Object> parsedData = docParserService.parseDoc(file);

        return ResponseEntity.ok(ApiMapper.mapDocumentFieldsToApiResponse(parsedData));
    }
    /*
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

     */
}
