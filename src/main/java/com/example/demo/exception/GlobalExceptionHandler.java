package com.example.demo.exception;

import com.example.demo.dto.respone.ApiRespone;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.swing.*;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiRespone<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiRespone<Void> apiResponse = ApiRespone.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiRespone<Void>> handlingRuntimeException(RuntimeException exception) {
        ApiRespone<Void> apiResponse = ApiRespone.<Void>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(exception.getMessage())
                .build();
        return ResponseEntity.internalServerError().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiRespone<Void>> handlingValidationException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getFieldError() != null ?
                exception.getFieldError().getDefaultMessage() : "Validation Error";

        Map<String,Object> attributes=null;
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        var constrainViolation = exception.getBindingResult()
                .getAllErrors().getFirst().unwrap(ConstraintViolation.class);
        attributes = constrainViolation.getConstraintDescriptor().getAttributes();
        log.info(attributes.toString());
        ApiRespone<Void> apiResponse = ApiRespone.<Void>builder()
                .code(errorCode.getCode())
                .message(Objects.nonNull(attributes) ?
                  mapAttributes(errorCode.getMessage(),attributes)
                        : errorCode.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    private String mapAttributes(String message, Map<String,Object> attributes){
        String minValue = String.valueOf(attributes.get("min"));

        return message.replace("{min}",minValue);
    }

}