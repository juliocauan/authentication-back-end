package br.com.juliocauan.authentication.infrastructure.exception;

public class PasswordMatchException extends RuntimeException {

    public PasswordMatchException(String message){
        super(message);
    }

}
