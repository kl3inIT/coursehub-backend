package com.coursehub.exceptions;

import com.coursehub.dto.ResponseGeneral;
import com.coursehub.exceptions.course.*;
import com.coursehub.exceptions.discount.DiscountDeletionNotAllowedException;
import com.coursehub.exceptions.discount.DiscountDuplicateException;
import com.coursehub.exceptions.discount.QuantityException;
import com.coursehub.exceptions.enrollment.EnrollNotFoundException;
import com.coursehub.exceptions.auth.*;
import com.coursehub.exceptions.category.CategoryNotFoundException;
import com.coursehub.exceptions.lesson.AccessDeniedException;
import com.coursehub.exceptions.lesson.LessonNotFoundException;
import com.coursehub.exceptions.lesson.LessonProgressNotFoundException;
import com.coursehub.exceptions.lesson.PreviousLessonNotFoundException;
import com.coursehub.exceptions.module.ModuleNotFoundException;
import com.coursehub.exceptions.report.ReportNotFoundException;
import com.coursehub.exceptions.module.PreviousModuleNotFoundException;
import com.coursehub.exceptions.s3.S3DeleteObjectException;
import com.coursehub.exceptions.s3.S3PresignUrlException;
import com.coursehub.exceptions.user.*;
import com.coursehub.exceptions.category.CategoryUsingException;
import com.coursehub.exceptions.review.ReviewNotFoundException;
import com.coursehub.exceptions.user.UserNotFoundException;
import com.coursehub.exceptions.comment.CommentNotFoundException;
import com.coursehub.exceptions.comment.CommentTooLongException;
import com.coursehub.exceptions.comment.ParentCommentNotFoundException;
import com.coursehub.exceptions.analytics.AnalyticsRetrievalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(UserAlreadyOwnsDiscountException.class)
    public ResponseEntity<ResponseGeneral<String>> handleUserAlreadyOwnsDiscountException(UserAlreadyOwnsDiscountException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from discount");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(QuantityException.class)
    public ResponseEntity<ResponseGeneral<String>> handleQuantityException(QuantityException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from quantity");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }


    // validate request data
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGeneral<List<String>>> handleValidationException(MethodArgumentNotValidException mex) {
        List<String> errors = mex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        ResponseGeneral<List<String>> response = new ResponseGeneral<>();
        response.setMessage("Validation Error");
        response.setData(errors);
        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidOtpException(InvalidOtpException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from otp");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalEmailException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEmailAlreadyExistsException(IllegalEmailException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from email");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEmailSendingException(EmailSendingException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Internal Server Error");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DiscountDeletionNotAllowedException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDiscountDeletionNotAllowedException(DiscountDeletionNotAllowedException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from discount deletion");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleOtpNotFoundException(OtpNotFoundException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Not Found");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResponseGeneral<String>> handlePasswordNotMatchException(PasswordNotMatchException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Bad request from password");
        response.setData(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<ResponseGeneral<String>> handleRedisOperationException(RedisOperationException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Internal Server Error");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDataNotFoundException(DataNotFoundException ex) {
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Data Not Found");
        response.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDataNotFoundException(InvalidTokenException ex) {
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

    @ExceptionHandler(EnrollNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleEnrollNotFoundException(EnrollNotFoundException ex) {
        log.error("Enrollment not found: {}", ex.getMessage());
        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Enrollment Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

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

    @ExceptionHandler(GenerateTokenException.class)
    public ResponseEntity<ResponseGeneral<String>> handleGenerateTokenException(GenerateTokenException ex) {
        log.error("Token generation failed: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Token Generation Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DiscountDuplicateException.class)
    public ResponseEntity<ResponseGeneral<String>> handleDiscountDuplicateException(DiscountDuplicateException ex) {
        log.error("Discount already exists: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Discount Already Exists");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseGeneral<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Access Denied");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Report Exception Handler

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleReportNotFoundException(ReportNotFoundException ex) {
        log.error("Report not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Report Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

    @ExceptionHandler(LessonProgressNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handleLessonProgressNotFoundException(LessonProgressNotFoundException ex) {
        log.error("Lesson progress not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Lesson Progress Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PreviousLessonNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handlePreviousLessonNotFoundException(PreviousLessonNotFoundException ex) {
        log.error("Previous lesson not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Previous Lesson Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PreviousModuleNotFoundException.class)
    public ResponseEntity<ResponseGeneral<String>> handlePreviousModuleNotFoundException(PreviousModuleNotFoundException ex) {
        log.error("Previous module not found: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Previous Module Not Found");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidCourseStatusException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidCourseStatusException(InvalidCourseStatusException ex) {
        log.error("Invalid course status: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Invalid Course Status");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ResponseGeneral<String>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Unauthorized Access");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidCourseLevelException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidCourseLevelException(InvalidCourseLevelException ex) {
        log.error("Invalid course level: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Invalid Course Level");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CourseUpdateException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCourseUpdateException(CourseUpdateException ex) {
        log.error("Course update failed: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Course Update Failed");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CourseAlreadyArchivedException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCourseAlreadyArchivedException(CourseAlreadyArchivedException ex) {
        log.error("Course already archived: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Course Already Archived");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CourseInvalidStateException.class)
    public ResponseEntity<ResponseGeneral<String>> handleCourseInvalidStateException(CourseInvalidStateException ex) {
        log.error("Course is in an invalid state: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Course Invalid State");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidCourseRestoreStateException.class)
    public ResponseEntity<ResponseGeneral<String>> handleInvalidCourseRestoreStateException(InvalidCourseRestoreStateException ex) {
        log.error("Invalid course restore state: {}", ex.getMessage());

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Invalid Course Restore State");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AnalyticsRetrievalException.class)
    public ResponseEntity<ResponseGeneral<String>> handleAnalyticsRetrievalException(AnalyticsRetrievalException ex) {
        log.error("Error retrieving analytics data: {}", ex.getMessage(), ex);

        ResponseGeneral<String> response = new ResponseGeneral<>();
        response.setMessage("Analytics Data Retrieval Error");
        response.setDetail(ex.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // generic phải để cuối vì nếu không sẽ bắt hết các exception khác
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
