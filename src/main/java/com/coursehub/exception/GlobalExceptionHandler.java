package com.coursehub.exception;

import com.coursehub.dto.ResponseGeneral;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.coursehub.exception.auth.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

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

    // validate request data
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGeneral<List<String>>> handleValidationException(MethodArgumentNotValidException mex){
        List<String> errors = mex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        ResponseGeneral<List<String>> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Validation Error");
        ResponseGeneral.setData(errors);
        return ResponseEntity.badRequest().body(ResponseGeneral);
    }


    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidOtpException(InvalidOtpException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Bad request from otp");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(ResponseGeneral);
    }

    @ExceptionHandler(IllegalEmailException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEmailAlreadyExistsException(IllegalEmailException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Bad request from email");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(ResponseGeneral);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEmailSendingException(EmailSendingException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Internal Server Error");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseGeneral);
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleOtpNotFoundException(OtpNotFoundException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Not Found");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseGeneral);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResponseGeneral<String>> handlePasswordNotMatchException(PasswordNotMatchException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Bad request from password");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(ResponseGeneral);
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ResponseGeneral<String>> handleRedisOperationException(RedisOperationException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Internal Server Error");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseGeneral);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDataNotFoundException(DataNotFoundException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Data Not Found");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseGeneral);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDataNotFoundException(InvalidTokenException ex){
        ResponseGeneral<String> ResponseGeneral = new ResponseGeneral<>();
        ResponseGeneral.setMessage("Forbidden");
        ResponseGeneral.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseGeneral);
    }

}
