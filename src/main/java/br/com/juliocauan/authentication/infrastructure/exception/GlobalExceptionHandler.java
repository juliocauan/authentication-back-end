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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ApiError standardError(Exception ex){
        return new ApiError()
            .timestamp(OffsetDateTime.now())
            .message(ex.getMessage());
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError responseError = standardError(ex);

        List<String> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(field -> field.getField() + ": " + field.getDefaultMessage())
            .collect(Collectors.toList());

        responseError.setFieldErrors(fieldErrors);
        responseError.setMessage("Input validation error!");
    
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExists(EntityExistsException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError(ex));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError(ex));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError(ex));
    }

    @ExceptionHandler(ExpiredPasswordResetException.class)
    public ResponseEntity<Object> handleExpiredPasswordReset(ExpiredPasswordResetException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError(ex));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(standardError(ex));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(standardError(ex));
    }

}
