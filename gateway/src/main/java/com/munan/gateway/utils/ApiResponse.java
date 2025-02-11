package com.munan.gateway.utils;

import com.fasterxml.jackson.annotation.*;
import java.io.Serializable;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private String message;
    private String code;
    private T data;
    private String error;
    private String error_description;

    private Object meta = new HashMap<>();

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, String code, T data) {
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public ApiResponse(String error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }
}
