package br.com.juliocauan.authentication.infrastructure.exception;

public class ExpiredPasswordResetTokenException extends RuntimeException {
    
    public ExpiredPasswordResetTokenException(String message) {
        super(message);
    }

}
