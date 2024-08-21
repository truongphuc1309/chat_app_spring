package com.truongphuc.constant;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    // GENERAL EXCEPTION CODE
    SERVER_ERROR ("1000", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT ("1001", HttpStatus.FORBIDDEN),
    INVALID_TOKEN ("1002", HttpStatus.BAD_REQUEST),
    INVALID_ROLE ("1003", HttpStatus.CONFLICT),

    // USER EXCEPTION CODE
    EXISTED_USER("2000", HttpStatus.CONFLICT),
    NON_EXISTED_USER("2001", HttpStatus.CONFLICT),
    UNAUTHORIZED("2002", HttpStatus.UNAUTHORIZED),

    // CONVERSATION EXCEPTION CODE
    NON_EXISTED_CONVERSATION("2001", HttpStatus.CONFLICT),
    INVALID_MEMBER("2002", HttpStatus.CONFLICT),

    //MESSAGE EXCEPTION CODE
    NON_EXISTED_MESSAGE("3001", HttpStatus.CONFLICT),
   ;

    ExceptionCode(String code, HttpStatusCode httpCode) {
        this.code = code;
        this.httpCode = httpCode;
    }

    private final String code;
    private final HttpStatusCode httpCode;
}
