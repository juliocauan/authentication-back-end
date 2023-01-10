package br.com.juliocauan.authentication.infrastructure.exception;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.openapitools.model.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ApiError responseError;

    private ApiError init(Exception ex){
        ApiError error = new ApiError();
        error.setTimestamp(OffsetDateTime.now());
        error.setMessage(ex.getMessage());
        return error;
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        responseError = init(ex);
        List<String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(field -> field.getField() + ", " + field.getDefaultMessage())
            .collect(Collectors.toList());
        List<String> globalErrors = ex.getBindingResult()
            .getGlobalErrors()
            .stream()
            .map(field -> field.getObjectName() + ", " + field.getDefaultMessage())
            .collect(Collectors.toList());

        responseError.setGlobalErrors(globalErrors);
        responseError.setFieldErrors(fieldErrors);
    
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExists(EntityExistsException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

}
