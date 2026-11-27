package com.shopflow.common.exception;

import com.shopflow.common.response.ApiResponse;
import com.shopflow.common.response.ApiResponseFactory;
import com.shopflow.common.response.DomainCode;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiResponseFactory apiResponseFactory;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBusiness(BaseException exception) {
        log.warn("Yêu cầu nghiệp vụ thất bại: {}", exception.getMessage());
        ApiResponse<?> body = apiResponseFactory.error(
                exception.getDomainCode(), exception.getMessage());
        return ResponseEntity.status(exception.getDomainCode().getHttpStatus()).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException exception) {
        log.warn("Từ chối truy cập: {}", exception.getMessage());
        ApiResponse<?> body = apiResponseFactory.error(DomainCode.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthentication(AuthenticationException exception) {
        log.warn("Xác thực thất bại: {}", exception.getMessage());
        ApiResponse<?> body = apiResponseFactory.error(DomainCode.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleBodyValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<?> body = apiResponseFactory.of(
                DomainCode.VALIDATION_ERROR,
                fieldErrors,
                String.join(",", fieldErrors.keySet()));
        return ResponseEntity.status(DomainCode.VALIDATION_ERROR.getHttpStatus()).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleParameterValidation(ConstraintViolationException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        ApiResponse<?> body = apiResponseFactory.of(
                DomainCode.VALIDATION_ERROR,
                fieldErrors,
                String.join(",", fieldErrors.keySet()));
        return ResponseEntity.status(DomainCode.VALIDATION_ERROR.getHttpStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleUnexpected(Exception exception) {
        log.error("Lỗi hệ thống ngoài dự kiến", exception);
        ApiResponse<?> body = apiResponseFactory.error(DomainCode.INTERNAL_ERROR);
        return ResponseEntity.status(DomainCode.INTERNAL_ERROR.getHttpStatus()).body(body);
    }
}
