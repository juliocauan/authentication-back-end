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
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityExistsException;

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
            .map(field -> field.getField() + ": " + field.getDefaultMessage())
            .collect(Collectors.toList());

        responseError.setFieldErrors(fieldErrors);
        responseError.setMessage("Input validation error!");
    
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExists(EntityExistsException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidOldPassword(InvalidPasswordException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(PasswordMatchException.class)
    public ResponseEntity<Object> handlePasswordConfirmation(PasswordMatchException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFound(UsernameNotFoundException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<Object> handleMailSend(MailSendException ex){
        responseError = init(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

}
