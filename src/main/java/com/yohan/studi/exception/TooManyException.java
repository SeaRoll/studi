package com.yohan.studi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyException extends RuntimeException {
    public TooManyException(String message) {
        super(message);
    }
}
