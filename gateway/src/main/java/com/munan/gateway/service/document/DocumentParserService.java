package com.munan.gateway.service.document;

import com.munan.gateway.domain.document.RequestMetadata;
import java.io.IOException;
import java.util.Map;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentParserService {
    Map<String, Object> parseDoc(MultipartFile file, RequestMetadata requestMetadata) throws IOException, TikaException;
}
