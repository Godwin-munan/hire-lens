package com.munan.gateway.utils;

import com.munan.gateway.enums.ModelTypes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

public class Util {

    public static final String TOKENIZER_MODEL_URL = "/models/en-nr-lang-tokens.bin";
    public static final String PERSON_LABEL = "PERSON";
    public static final String SKILLS_LABEL = "SKILLS";

    @NotNull
    public static String getClientIp(HttpServletRequest exchange) {
        String clientIp = exchange.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_X_FORWARDED");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_FORWARDED_FOR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_FORWARDED");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("HTTP_VIA");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = exchange.getHeader("REMOTE_ADDR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equals(clientIp) || exchange.getRemoteAddr() != null) {
            clientIp = exchange.getRemoteAddr();
        }
        // If all attempts fail, use a default value or return null
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = "0.0.0.0";
        }
        return clientIp;
    }
}
