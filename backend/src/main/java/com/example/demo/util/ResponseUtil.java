package com.example.demo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {

    public static <T> ResponseEntity<?> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK.value());
        response.put("message", "Success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<?> success() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK.value());
        response.put("message", "Success");
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<?> error(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", status.value());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<?> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseEntity<?> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static ResponseEntity<?> internalError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}