package com.munan.gateway.web.rest.document;

import com.munan.gateway.domain.document.RequestMetadata;
import com.munan.gateway.service.document.DocumentParserService;
import com.munan.gateway.service.languageModel.FileSanitizationService;
import com.munan.gateway.utils.ApiResponse;
import com.munan.gateway.utils.Util;
import com.munan.gateway.utils.mapper.ApiMapper;
import com.munan.gateway.web.rest.errors.FileEmptyException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class DocumentParserResource {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserResource.class);
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
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request)
        throws TikaException, IOException, FileEmptyException {
        if (file.isEmpty()) {
            throw new FileEmptyException("Oops! File is empty, please check.");
        }

        RequestMetadata requestMetadata = new RequestMetadata();

        requestMetadata.setRequestTime(Instant.now());
        requestMetadata.setClientIp(Util.getClientIp(request));
        requestMetadata.setUserAgent(Util.getUserAgent(request));
        requestMetadata.setRequestUri(request.getRequestURI() != null ? request.getRequestURI() : "unknown request-uri");
        requestMetadata.setHttpMethod(request.getMethod() != null ? request.getMethod() : "unknown request-method");

        // Pass file to service for parsing
        Map<String, Object> parsedData = docParserService.parseDoc(file, requestMetadata);

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
