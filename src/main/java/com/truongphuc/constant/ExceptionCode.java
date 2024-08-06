package com.truongphuc.constant;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    SERVER_ERROR ("1000", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT ("1001", HttpStatus.FORBIDDEN),

    EXISTED_USER("2000", HttpStatus.CONFLICT),
    NON_EXISTED_USER("2001", HttpStatus.CONFLICT),
    UNAUTHORIZED("2002", HttpStatus.UNAUTHORIZED)
   ;

    ExceptionCode(String code, HttpStatusCode httpCode) {
        this.code = code;
        this.httpCode = httpCode;
    }

    private final String code;
    private final HttpStatusCode httpCode;
}
