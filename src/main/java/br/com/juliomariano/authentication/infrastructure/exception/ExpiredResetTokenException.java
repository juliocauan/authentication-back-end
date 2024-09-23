package br.com.juliomariano.authentication.infrastructure.exception;

public class ExpiredResetTokenException extends RuntimeException {
    
    public ExpiredResetTokenException(String message) {
        super(message);
    }

}
