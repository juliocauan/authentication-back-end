package br.com.juliocauan.authentication.infrastructure.exception;

public class ExpiredRecoveryTokenException extends RuntimeException {
    
    public ExpiredRecoveryTokenException(String message) {
        super(message);
    }

}
