package dev.erpix.easykan.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends RuntimeException {

    private final HttpStatus status;

    public RestException(HttpStatus status) {
        this.status = status;
    }

    public RestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
