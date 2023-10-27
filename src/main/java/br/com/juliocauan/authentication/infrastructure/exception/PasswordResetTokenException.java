package br.com.juliocauan.authentication.infrastructure.exception;

public class PasswordResetTokenException extends RuntimeException {
    
    public PasswordResetTokenException(String message) {
        super(message);
    }

}
