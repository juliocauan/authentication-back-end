package br.com.juliocauan.authentication.infrastructure.exception;

import org.openapitools.model.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private CustomError responseError;

    private CustomError init(int code, Exception ex){
        CustomError error = new CustomError();
        error.setCode(code);
        error.setTrace(stackTraceString(ex.getStackTrace().toString()));
        error.setMessage(ex.getMessage());
        return error;
    }

    private String stackTraceString(String elements){
        return StringUtils.trimTrailingCharacter(elements, '\n');
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExists(EntityExistsException ex){
        responseError = init(101, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex){
        responseError = init(201, ex);
        responseError.message("Invalid User or Password!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseError);
    }

}
