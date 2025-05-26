package com.coursehub.exception;

import com.coursehub.dto.ResponseDTO;
import com.coursehub.exception.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // validate request data
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<List<String>>> handleValidationException(MethodArgumentNotValidException mex){
        List<String> errors = mex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        ResponseDTO<List<String>> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Validation Error");
        responseDTO.setData(errors);
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> handleException(Exception ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Internal Server Error");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ResponseDTO<String>> handleInvalidOtpException(InvalidOtpException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Bad request from otp");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO<String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Bad request from email");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ResponseDTO<String>> handleEmailSendingException(EmailSendingException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Internal Server Error");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> handleOtpNotFoundException(OtpNotFoundException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Not Found");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResponseDTO<String>> handlePasswordNotMatchException(PasswordNotMatchException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Bad request from password");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(responseDTO);
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ResponseDTO<String>> handleRedisOperationException(RedisOperationException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Internal Server Error");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> handleDataNotFoundException(DataNotFoundException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Data Not Found");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseDTO<String>> handleDataNotFoundException(InvalidTokenException ex){
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        responseDTO.setMessage("Forbidden");
        responseDTO.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
    }












}
