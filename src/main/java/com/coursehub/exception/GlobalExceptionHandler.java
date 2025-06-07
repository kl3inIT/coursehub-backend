package com.coursehub.exception;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.exception.auth.*;
import com.coursehub.exception.category.CategoryNotFoundException;
import com.coursehub.exception.comment.CommentNotFoundException;
import com.coursehub.exception.comment.CommentTooLongException;
import com.coursehub.exception.comment.ParentCommentNotFoundException;
import com.coursehub.exception.course.CourseCreationException;
import com.coursehub.exception.course.CourseNotFoundException;
import com.coursehub.exception.course.FileUploadException;
import com.coursehub.exception.course.InvalidFileException;
import com.coursehub.exception.lesson.LessonNotFoundException;
import com.coursehub.exception.module.ModuleNotFoundException;
import com.coursehub.exception.s3.S3DeleteObjectException;
import com.coursehub.exception.s3.S3PresignUrlException;
import com.coursehub.exception.user.*;
import com.coursehub.exception.category.CategoryUsingException;
import com.coursehub.exception.review.ReviewNotFoundException;
import com.coursehub.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseGeneral<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        
        ResponseGeneral<Object> response = new ResponseGeneral<>();
        response.setMessage("Invalid request");
        response.setDetail(ex.getMessage());
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
        List<String> errors = mex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        ResponseGeneral<List<String>> response = new ResponseGeneral<>();
        response.setMessage("Validation Error");
        response.setData(errors);
        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidOtpException(InvalidOtpException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from otp");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalEmailException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEmailAlreadyExistsException(IllegalEmailException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from email");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEmailSendingException(EmailSendingException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Internal Server Error");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleOtpNotFoundException(OtpNotFoundException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Not Found");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResponseGeneral<String>> handlePasswordNotMatchException(PasswordNotMatchException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from password");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ResponseGeneral<String>> handleRedisOperationException(RedisOperationException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Internal Server Error");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDataNotFoundException(DataNotFoundException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Data Not Found");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDataNotFoundException(InvalidTokenException ex){
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Forbidden");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Course-specific exception handlers
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCourseNotFoundException(CourseNotFoundException ex) {
        log.error("Course not found: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Course Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CourseCreationException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCourseCreationException(CourseCreationException ex) {
        log.error("Course creation failed: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Course Creation Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidFileException(InvalidFileException ex) {
        log.error("Invalid file: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Invalid File");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ResponseGeneral<String>> handleFileUploadException(FileUploadException ex) {
        log.error("File upload failed: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("File Upload Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        log.error("Category not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Category Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CategoryUsingException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCategoryInUseException(CategoryUsingException ex) {
        log.error("Category using: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Category Using");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // User Exception Handler
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleReviewNotFoundException(ReviewNotFoundException ex) {
        log.error("Review not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Review Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("User Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AvatarUploadException.class)
    public ResponseEntity<ResponseGeneral<String>> handleAvatarUploadException(AvatarUploadException ex) {
        log.error("Avatar upload failed: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Avatar Upload Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserDeletionException.class)
    public ResponseEntity<ResponseGeneral<String>> handleUserDeletionException(UserDeletionException ex) {
        log.error("User deletion failed: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Cannot Delete User");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ResponseGeneral<String>> handleSamePasswordException(SamePasswordException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("New password must be different from current password");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ResponseGeneral<String>> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Current password is incorrect");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    //module

    @ExceptionHandler(ModuleNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleModuleNotFoundException(ModuleNotFoundException ex) {
        log.error("Module not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Module Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(S3DeleteObjectException.class)
    public ResponseEntity<ResponseGeneral<String>> handleS3DeleteObjectException(S3DeleteObjectException ex) {
        log.error("S3 delete object failed: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("S3 Delete Object Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(S3PresignUrlException.class)
    public ResponseEntity<ResponseGeneral<String>> handleS3PresignUrlException(S3PresignUrlException ex) {
        log.error("S3 presign URL generation failed: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("S3 Presign URL Generation Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(LessonNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleLessonNotFoundException(LessonNotFoundException ex) {
        log.error("Lesson not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Lesson Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Comment Exception Handlers

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCommentNotFoundException(CommentNotFoundException ex) {
        log.error("Comment not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Comment Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CommentTooLongException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCommentToLongException(CommentTooLongException ex) {
        log.error("Comment too long: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Comment Too Long");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ParentCommentNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleParentCommentNotFoundException(ParentCommentNotFoundException ex) {
        log.error("Parent comment not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Parent Comment Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
