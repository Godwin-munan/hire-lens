package com.munan.hirelens.service.document;

import java.io.IOException;
import java.util.Map;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentParserService {
    Map<String, Object> parseDoc(MultipartFile file) throws IOException, TikaException;
}
