package br.com.juliocauan.authentication.infrastructure.exception;

public class InvalidPasswordException extends RuntimeException {
    
    public InvalidPasswordException(String message){
        super(message);
    }

}
