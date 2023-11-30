package br.com.juliocauan.authentication.domain.service.util;

import org.openapitools.model.PasswordMatch;

import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;

public abstract class PasswordService {

    public final void validatePasswordMatch(PasswordMatch passwordMatch){
        String encodedPassword = encode(passwordMatch.getPassword());
        String rawPassword = passwordMatch.getPasswordConfirmation();
        validatePasswordMatch(rawPassword, encodedPassword);
    }

    public final void validatePasswordMatch(String rawPassword, String encodedPassword){
        if(!matches(rawPassword, encodedPassword))
            throw new InvalidPasswordException("Passwords don't match!");
    }

    public abstract String encode(String password);
    protected abstract boolean matches(String rawPassword, String encodedPassword);

}
