package io.github.cheatbook.amzrevenuemanager.interfaces.web.advice;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.github.cheatbook.amzrevenuemanager.domain.exception.BusinessException;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 全コントローラー共通の例外ハンドラー
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 業務例外 (BusinessException) のハンドリング
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Business exception occurred: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, ex.getStatus());
    }

    /**
     * バリデーション例外 (MethodArgumentNotValidException) のハンドリング
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation failed: {}", details);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "Validation failed: " + details,
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, status);
    }

    /**
     * その他の未ハンドリング例外のハンドリング
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "An unexpected error occurred. Please contact the administrator.",
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, status);
    }
}
