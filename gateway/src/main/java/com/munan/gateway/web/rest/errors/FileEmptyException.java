package com.munan.gateway.web.rest.errors;

public class FileEmptyException extends RuntimeException {

    private String message;

    public FileEmptyException(String message) {
        super(message);
    }
}
