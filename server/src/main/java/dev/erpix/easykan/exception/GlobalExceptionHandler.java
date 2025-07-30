package dev.erpix.easykan.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponse> handleRestException(RestException ex) {
        ErrorResponse res = ErrorResponse.create(ex, ex.getStatus(), ex.getMessage());
        return new ResponseEntity<>(res, ex.getStatus());
    }

}
