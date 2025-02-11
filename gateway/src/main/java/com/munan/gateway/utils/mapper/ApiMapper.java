package com.munan.gateway.utils.mapper;

import static com.munan.gateway.utils.AppCode.OKAY;
import static com.munan.gateway.utils.MessageUtil.APPLICANT_DETAIL_SUCCESSFULLY;

import com.munan.gateway.utils.ApiResponse;
import java.util.Map;

public class ApiMapper {

    public static ApiResponse<Map<String, Object>> mapDocumentFieldsToApiResponse(Map<String, Object> source) {
        ApiResponse<Map<String, Object>> result = new ApiResponse<>();
        result.setCode(OKAY);
        result.setData(source);
        result.setMessage(APPLICANT_DETAIL_SUCCESSFULLY);
        return result;
    }

    public static ApiResponse<Map<String, Object>> mapDocumentErrorToApiResponse(Map<String, Object> source) {
        ApiResponse<Map<String, Object>> result = new ApiResponse<>();
        result.setCode(OKAY);
        result.setData(source);
        result.setMessage(APPLICANT_DETAIL_SUCCESSFULLY);
        return result;
    }
}
