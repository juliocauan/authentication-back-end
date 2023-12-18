package br.com.juliocauan.authentication.infrastructure.exception;

public class ExpiredPasswordResetException extends RuntimeException {
    
    public ExpiredPasswordResetException(String message) {
        super(message);
    }

}
