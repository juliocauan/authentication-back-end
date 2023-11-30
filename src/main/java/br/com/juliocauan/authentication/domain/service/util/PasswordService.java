package br.com.juliocauan.authentication.domain.service.util;

import org.openapitools.model.PasswordMatch;

import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordMatchException;

public abstract class PasswordService {

    public final void checkPasswordConfirmation(PasswordMatch passwordMatch){
        String newPassword = encode(passwordMatch.getPassword());
        String confirmationPassword = passwordMatch.getPasswordConfirmation();
        if(!matches(confirmationPassword, newPassword))
            throw new PasswordMatchException("Confirmation and new password are different!");
    }

    public final void checkCurrentPassword(String encodedPassword, String rawPassword){
        if(!matches(rawPassword, encodedPassword))
            throw new InvalidPasswordException("Wrong current password!");
    }

    public abstract String encode(String password);
    public abstract boolean matches(String rawPassword, String encodedPassword);

}
