package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User already existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    INVALID_KEY(1003, "Invalid validation key {min}", HttpStatus.BAD_REQUEST),
    USER_NOT_AUTHENTED(1004, "User not authented", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}