package com.munan.gateway.utils;

import static com.munan.gateway.utils.MessageUtil.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public class ResponseUtils {

    public static ApiResponse paginatedResponse(Page<?> page, String responseMessage, String status) {
        ApiResponse<List<?>> result = new ApiResponse<>(responseMessage, status, page.getContent());

        Map<String, Object> map = new HashMap<>();
        map.put(PAGEABLE, page.getPageable());
        map.put(TOTAL_RECORDS, page.getTotalElements());
        map.put(TOTAL_PAGES, page.getTotalPages());

        result.setMeta(map);
        return result;
    }
}
