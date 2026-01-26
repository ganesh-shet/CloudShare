package com.ganesh.cloudshare.exceptions;

import com.mongodb.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleDuplicateEmailException(RuntimeException e) {
        Map<String,Object> data = new HashMap<>();
        data.put("status", HttpStatus.CONFLICT);
        data.put("message", e.getMessage());  //Email already exists
        return ResponseEntity.status(HttpStatus.CONFLICT).body(data);
    }
}
