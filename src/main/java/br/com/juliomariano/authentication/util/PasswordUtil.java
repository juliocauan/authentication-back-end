package br.com.juliomariano.authentication.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openapitools.model.PasswordMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.juliomariano.authentication.infrastructure.exception.PasswordException;

@Component
public final class PasswordUtil {

    private static PasswordEncoder encoder;
    private static Environment env;

    @Autowired
    private void setPasswordEncoder(PasswordEncoder encoder) {
        PasswordUtil.encoder = encoder;
    }

    @Autowired
    private void setEnvironment(Environment env) {
        PasswordUtil.env = env;
    }

    public static String encode(String password) {
        return encoder.encode(password);
    }

    public static void validateMatch(String rawPassword, String encodedPassword) {
        if(!matches(rawPassword, encodedPassword))
            throw new PasswordException("Passwords don't match!");
    }

    public static void validateMatch(PasswordMatch passwordMatch) {
        String rawPassword = passwordMatch.getPassword();
        String encodedPassword = encode(passwordMatch.getPasswordConfirmation());
        validateMatch(rawPassword, encodedPassword);
    }

    public static void validateSecurity(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=.*[\\d]).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if(!matcher.matches())
            throw new PasswordException("Password is not strong!");
    }

    public static void validateAdminKey(String adminPassword) {
        if(!matches(adminPassword, encode(env.getProperty("ADMIN_KEY"))))
            throw new PasswordException("Admin Key is incorrect!");
    }

    private static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

}
