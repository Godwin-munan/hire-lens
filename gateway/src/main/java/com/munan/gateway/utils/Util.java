package com.munan.gateway.utils;

import com.munan.gateway.enums.ModelTypes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.multipart.MultipartFile;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static final String TOKENIZER_MODEL_URL = "/models/en-nr-lang-tokens.bin";
    public static final String PERSON_LABEL = "PERSON";
    public static final String SKILLS_LABEL = "SKILLS";

    public static final String LINKEDIN_LINK = "linkedIn";
    public static final String GITHUB_LINK = "github";

    @Value("${FILE.UPLOAD.MAX-SIZE.IMAGE}")
    public static long MAX_IMAGE_SIZE_BYTES;

    public static final Long MAX_IMAGE_SIZE_KB = MAX_IMAGE_SIZE_BYTES * 1024;

    @Value("${FILE.UPLOAD.MAX-SIZE.DOCUMENT}")
    public static long MAX_DOCUMENT_SIZE_BYTES;

    public static final Long MAX_DOCUMENT_SIZE_KB = MAX_DOCUMENT_SIZE_BYTES * 1024;
    public static final String TOKEN_TYPE = "Bearer";
    public static final String VAT = "7.5%";
    public static final Double VAT_VALUE = 7.5;

    private static double convertBytesToKB(long bytes) {
        return bytes / 1024.0;
    }

    private static double convertBytesToMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    public static final int FILE_SIZE_DECIMAL_PLACE = 2;

    public static double roundUpFileSize(double value, int decimalPlaces) {
        double factor = Math.pow(10, decimalPlaces);
        return Math.ceil(value * factor) / factor;
    }

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    public static boolean isPasswordLengthInvalid(String password) {
        return (StringUtils.isEmpty(password) || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH);
    }

    public static String extractBase64Content(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex >= 0 ? filename.substring(lastDotIndex) : "";
    }

    public static String generate24DigitID() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString().replaceAll("-", "").substring(0, 24));
    }

    public static String generate8DigitID() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString().substring(0, 8));
    }

    public static String generate3DigitID() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString().replaceAll("-", "").substring(0, 3));
    }

    public static String generate24DigitIDV2() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString().substring(0, 24));
    }

    public static String generateUniqueId() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString());
    }

    public static Long generateUniqueId2() {
        long val = -1;
        do {
            val = UUID.randomUUID().getMostSignificantBits();
        } while (val < 0);
        return val;
    }

    public static Long generateUniqueId3() {
        Long val = -1L;
        final UUID uid = UUID.randomUUID();
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uid.getLeastSignificantBits());
        buffer.putLong(uid.getMostSignificantBits());
        final BigInteger bi = new BigInteger(buffer.array());
        val = bi.longValue() & Long.MAX_VALUE;
        return val;
    }

    public static String getFormatted(Double amount) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(amount);
    }

    public static String convertLocalDateTime(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return "";
        }
        String pattern = "EEE MMM dd yyyy HH:mm:ss zzz";
        //SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        //format.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        //ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("GMT+05:30"));
        //DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(zdt);
    }

    public static String convertLocalDateTimeToDate(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return "";
        }
        String pattern = "dd MMMM, yyyy";
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("GMT+05:30"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(zdt);
    }

    public static String convertLocalDateTimeToDateV2(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return "";
        }
        String pattern = "dd-MM-yyyy";
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("GMT+05:30"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(zdt);
    }

    public static String convertLocalDateTimeToTime(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return "";
        }
        String pattern = "hh:mm:ss a";
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("GMT+05:30"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(zdt);
    }

    public static String getDateString(Date date) {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentDateTime() {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    public static String getCurrentDate() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    public static String getCurrentTime() {
        String pattern = "hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    public static String getStringToDate(String date) {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date d = dateFormat.parse(date);
            Timestamp ts = new Timestamp(d.getTime());
            return ts.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime getDateFromString(String date) {
        try {
            DateTimeFormatter ft = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss a");
            return LocalDateTime.parse(date, ft);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime getDateFromStringV2(String date) {
        log.info("DATE RECEIVED: {}", date);
        if (Objects.isNull(date)) {
            return null;
        }
        if ("".equals(date.trim())) {
            return null;
        }
        String newDate = date.toLowerCase();

        LocalDateTime dateTime = null;
        try {
            if (
                newDate.startsWith("mon") ||
                newDate.startsWith("tue") ||
                newDate.startsWith("wed") ||
                newDate.startsWith("thu") ||
                newDate.startsWith("fri") ||
                newDate.startsWith("sat") ||
                newDate.startsWith("sun")
            ) {
                //eg. Mon Apr 01 21:01:44 UTC 2024
                dateTime = getDateFromStringV3(date);
                //System.out.println("########## DATE TIME: " + dateTime);
            }
            if (Objects.nonNull(dateTime)) {
                date = formatLocalDateTime(dateTime);
                //System.out.println("########## DATE HERE: " + date);
            }
            // Truncate the nanoseconds part to fit the supported format
            String truncatedDate = date.substring(0, 19); // Truncate the nanoseconds part
            // Define the formatter to parse the truncated date string
            DateTimeFormatter ft = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            return LocalDateTime.parse(truncatedDate, ft);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static LocalDateTime getDateFromStringV3(String date) {
        if (Objects.isNull(date)) {
            return null;
        }
        if ("".equals(date.trim())) {
            return null;
        }
        try {
            //Mon Apr 01 21:01:44 UTC 2024
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy");
            return LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileSizeInKB(byte[] bytes) {
        if (Objects.nonNull(bytes) && bytes.length > 0) {
            // Calculate file size in kilobytes
            double fileSizeInKB = (double) bytes.length / 1024;
            // Round to two decimal places
            String roundedSize = String.format("%.2f", fileSizeInKB);
            return roundedSize;
        } else {
            // File doesn't exist or is not a regular file
            return "-1"; // You can choose to handle this differently based on your requirement
        }
    }

    public static String getFileSizeInKB(MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            // Get file size in bytes
            long fileSizeInBytes = multipartFile.getSize();
            // Convert bytes to kilobytes
            double fileSizeInKB = (double) fileSizeInBytes / 1024;
            // Round to two decimal places
            String roundedSize = String.format("%.2f", fileSizeInKB);
            return roundedSize;
        } else {
            // File doesn't exist or is empty
            return "-1"; // You can choose to handle this differently based on your requirement
        }
    }

    public static byte[] convertBase64ToByteArray(String base64Image) {
        // Remove data URL prefix if it exists
        String base64ImageWithoutPrefix = base64Image.replaceAll("data:image/[^;]+;base64,", "");
        // Decode Base64 string to byte array
        byte[] imageByteArray = Base64.getDecoder().decode(base64ImageWithoutPrefix);
        return imageByteArray;
    }

    public static MultipartFile convertBase64ToMultipartFile(String base64String) throws IOException {
        // Decode Base64 string to byte array
        byte[] decodedBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64String);

        // Create a MultipartFile object from byte array
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(decodedBytes);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                new FileOutputStream(dest).write(decodedBytes);
            }
        };

        return multipartFile;
    }

    public static String getFileExtension(MultipartFile file) {
        if (Objects.nonNull(file)) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                return originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            return "";
        }
        return "";
    }

    public static String getNewFileName(String filename) {
        String ext = getFileExtension(filename);
        return "file-" + generate8RandomDigits() + "-" + getCurrentDateV2() + ext;
    }

    public static String getNewFileNameV2(String filename) {
        return "file-" + generate24RandomDigits() + "-" + getCurrentDateTimeV2() + "-" + filename;
    }

    public static String getNewFileName(MultipartFile file) {
        //return "file_" + generate24RandomDigits() + "_" + getCurrentDateTimeV2() + getFileExtension(file);
        return getNewFileName(file.getName());
    }

    public static String getCurrentDateTimeV2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        return dateFormat.format(new Date()).replaceAll("/", "_") + "@" + timeFormat.format(new Date()).replaceAll(":", "_");
    }

    public static String getCurrentDateV2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date()).replaceAll("/", "");
    }

    public static String generate24RandomDigits() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString().replaceAll("-", "").substring(0, 24));
    }

    public static String generate8RandomDigits() {
        UUID uuid = UUID.randomUUID();
        return (uuid.toString().replaceAll("-", "").substring(0, 8));
    }

    public static String getCurrencyFormatter(Double amount) {
        return new DecimalFormat("#,###.00").format(amount);
    }

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

    @NotNull
    public static String getUserAgent(HttpServletRequest exchange) {
        String userAgent = exchange.getHeader("User-Agent");

        if (userAgent == null || userAgent.isEmpty() || "unknown".equalsIgnoreCase(userAgent)) {
            userAgent = "Unknown User-Agent";
        }
        return userAgent;
    }
}
