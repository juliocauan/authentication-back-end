package br.com.juliocauan.authentication.infrastructure.exception;

public class PasswordConfirmationException extends RuntimeException {

    public PasswordConfirmationException(String message){
        super(message);
    }

}
