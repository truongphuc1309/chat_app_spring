package com.truongphuc.exception;

import com.truongphuc.constant.ExceptionCode;
import com.truongphuc.dto.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
        ApiResponse<?> res = new ApiResponse<>();
        res.setCode(exception.getCode());
        res.setMessage(exception.getMessage());

        return new ResponseEntity<>(res, exception.getHttpCode());
    }

    @ExceptionHandler (value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleArgumentException(MethodArgumentNotValidException exception) {
        ApiResponse<?> res = new ApiResponse<>();

        ExceptionCode exceptionCode = ExceptionCode.INVALID_ARGUMENT;
        res.setCode(exceptionCode.getCode());
        res.setMessage(Objects.requireNonNull(exception.getFieldError()).getDefaultMessage());

        return new ResponseEntity<>(res, exceptionCode.getHttpCode());
    }

    @ExceptionHandler (value = AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException exception) {
        ApiResponse<?> res = new ApiResponse<>();

        ExceptionCode exceptionCode = ExceptionCode.UNAUTHENTICATED;
        res.setCode(exceptionCode.getCode());
        res.setMessage(exception.getMessage());

        return new ResponseEntity<>(res, exceptionCode.getHttpCode());
    }

    @ExceptionHandler (value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleNotReadableException(HttpMessageNotReadableException exception) {
        ApiResponse<?> res = new ApiResponse<>();

        ExceptionCode exceptionCode = ExceptionCode.INVALID_ARGUMENT;
        res.setCode(exceptionCode.getCode());
        res.setMessage("Missing Arguments");

        return new ResponseEntity<>(res, exceptionCode.getHttpCode());
    }


    @ExceptionHandler (value = BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException (BadCredentialsException exception){
        ApiResponse<?> res = new ApiResponse<>();

        ExceptionCode exceptionCode = ExceptionCode.UNAUTHORIZED;
        res.setCode(exceptionCode.getCode());
        res.setMessage("Password or Email is incorrect");

        return new ResponseEntity<>(res, exceptionCode.getHttpCode());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception exception) {
        log.error("EXCEPTION ", exception);

        ApiResponse<?> res = new ApiResponse<>();

        ExceptionCode exceptionCode = ExceptionCode.SERVER_ERROR;
        res.setCode(exceptionCode.getCode());
        res.setMessage(exception.getMessage());

        return new ResponseEntity<>(res, exceptionCode.getHttpCode());
    }

}
