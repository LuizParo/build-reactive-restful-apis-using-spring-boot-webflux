package com.learnreactivespring.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class HttpStatusNotFoundException extends RuntimeException {

    public HttpStatusNotFoundException(String message) {
        super(message);
    }
}
