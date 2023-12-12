package br.com.juliocauan.authentication.domain.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openapitools.model.PasswordMatch;

import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;

public abstract class PasswordService {

    public final void validatePasswordMatch(PasswordMatch passwordMatch) {
        String encodedPassword = encode(passwordMatch.getPassword());
        String rawPassword = passwordMatch.getPasswordConfirmation();
        validatePasswordMatch(rawPassword, encodedPassword);
    }

    public final void validatePasswordMatch(String rawPassword, String encodedPassword) {
        if(!matches(rawPassword, encodedPassword))
            throw new InvalidPasswordException("Passwords don't match!");
    }

    public final void validatePasswordSecurity(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=.*[\\d]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if(!matcher.matches())
            throw new InvalidPasswordException("Password is not strong. It must have at least: " + 
                "1 lower case character, " + 
                "1 upper case character, " +
                "1 special character, " +
                "1 number");
    }

    public abstract String encode(String password);
    public abstract void validateAdminPassword(String adminPassword);
    protected abstract boolean matches(String rawPassword, String encodedPassword);

}
