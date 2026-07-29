package com.example.demo.exception;

import com.example.demo.dto.respone.ApiRespone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
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

        ApiRespone<Void> apiResponse = ApiRespone.<Void>builder()
                .code(ErrorCode.INVALID_KEY.getCode())
                .message(errorMessage)
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }
}