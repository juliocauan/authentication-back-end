package br.com.juliomariano.authentication.infrastructure.exception;

public class PasswordException extends RuntimeException {
    
    public PasswordException(String message){
        super(message);
    }

}
