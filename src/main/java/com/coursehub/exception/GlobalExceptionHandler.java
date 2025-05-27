package com.coursehub.exception;

import com.coursehub.dto.ResponseGeneral;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseGeneral<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ResponseGeneral<Object> response = new ResponseGeneral<>();
        response.setMessage("Resource not found");
        response.setDetail(ex.getMessage());
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseGeneral<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        
        ResponseGeneral<Object> response = new ResponseGeneral<>();
        response.setMessage("Invalid request");
        response.setDetail(ex.getMessage());
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseGeneral<Object>> handleMaxUploadSizeException(MaxUploadSizeExceededException ex) {
        log.error("File size exceeded: {}", ex.getMessage());
        
        ResponseGeneral<Object> response = new ResponseGeneral<>();
        response.setMessage("File size too large");
        response.setDetail("Maximum upload size exceeded");
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseGeneral<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ResponseGeneral<Object> response = new ResponseGeneral<>();
        response.setMessage("Internal server error");
        response.setDetail("An unexpected error occurred");
        response.setData(null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
