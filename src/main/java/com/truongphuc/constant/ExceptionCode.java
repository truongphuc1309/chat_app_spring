package com.truongphuc.constant;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    // GENERAL EXCEPTION CODE
    SERVER_ERROR ("1000", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT ("1001", HttpStatus.FORBIDDEN),
    INVALID_ROLE ("1002", HttpStatus.CONFLICT),

    // USER EXCEPTION CODE
    EXISTED_USER("2000", HttpStatus.CONFLICT),
    NON_EXISTED_USER("2001", HttpStatus.CONFLICT),
    UNAUTHORIZED("2002", HttpStatus.UNAUTHORIZED),
    INACTIVE_USER("2003", HttpStatus.CONFLICT),

    // CONVERSATION EXCEPTION CODE
    NON_EXISTED_CONVERSATION("3001", HttpStatus.CONFLICT),
    INVALID_MEMBER("3002", HttpStatus.CONFLICT),
    EXISTED_CONVERSATION("3003", HttpStatus.CONFLICT),


    //MESSAGE EXCEPTION CODE
    NON_EXISTED_MESSAGE("4001", HttpStatus.CONFLICT),
    NON_MATCHED_MESSAGE("4002", HttpStatus.CONFLICT),

    // JWT EXCEPTION CODE
    INVALID_TOKEN ("4001", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN ("4002", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED("4003", HttpStatus.FORBIDDEN),

    // UPLOAD EXCEPTION CODE
    INVALID_FILE_SIZE("5001", HttpStatus.CONFLICT),
    INVALID_FILE_TYPE ("5002", HttpStatus.CONFLICT),

    // Mail
    OVER_LIMIT("6001", HttpStatus.CONFLICT),
   ;

    ExceptionCode(String code, HttpStatusCode httpCode) {
        this.code = code;
        this.httpCode = httpCode;
    }

    private final String code;
    private final HttpStatusCode httpCode;
}
