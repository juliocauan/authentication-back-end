package br.com.juliocauan.authentication.infrastructure.service.util;

import org.openapitools.model.PasswordMatch;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.exception.InvalidOldPasswordException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordConfirmationException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordService {

    private final PasswordEncoder encoder;

    public final void checkPasswordConfirmation(PasswordMatch passwordMatch){
        String newPassword = passwordMatch.getPassword();
        String confirmationPassword = passwordMatch.getPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }

    public final void checkCurrentPassword(String encodedPassword, String rawPassword){
        if(!encoder.matches(rawPassword, encodedPassword))
            throw new InvalidOldPasswordException("Wrong current password!");
    }

    public final String encodePassword(String password) {
        return encoder.encode(password);
    }

}
