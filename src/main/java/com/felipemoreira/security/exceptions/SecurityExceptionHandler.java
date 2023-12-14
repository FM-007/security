package com.felipemoreira.security.exceptions;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class SecurityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExistsException(EntityExistsException exception) {
        return buildResponseEntity(BAD_REQUEST, exception.getMessage(), singletonList(exception.getMessage()));
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, String message, List<String> errors) {
        ApiError apiError = ApiError.builder()
                .code(httpStatus.value())
                .status(httpStatus.getReasonPhrase())
                .message(message)
                .timestamp(now())
                .build();

        return ResponseEntity.status(httpStatus).body(apiError);
    }
}
