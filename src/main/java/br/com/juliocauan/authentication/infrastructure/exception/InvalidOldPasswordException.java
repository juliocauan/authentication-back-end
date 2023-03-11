package br.com.juliocauan.authentication.infrastructure.exception;

public class InvalidOldPasswordException extends RuntimeException {
    
    public InvalidOldPasswordException(String message){
        super(message);
    }

}
