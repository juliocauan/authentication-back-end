package br.com.juliocauan.authentication.infrastructure.exception;

public class ExpiredResetTokenException extends RuntimeException {
    
    public ExpiredResetTokenException(String message) {
        super(message);
    }

}
