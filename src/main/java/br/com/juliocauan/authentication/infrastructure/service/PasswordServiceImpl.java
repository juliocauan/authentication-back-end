package br.com.juliocauan.authentication.infrastructure.service;

import org.openapitools.model.PasswordLinkUpdate;
import org.openapitools.model.PasswordUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.PasswordService;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidOldPasswordException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordConfirmationException;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder encoder;

    public final void checkPasswordConfirmation(PasswordLinkUpdate passwordUpdate){
        String newPassword = passwordUpdate.getNewPassword();
        String confirmationPassword = passwordUpdate.getNewPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }
    
    public final void checkPasswordConfirmation(PasswordUpdate passwordUpdate){
        String newPassword = passwordUpdate.getNewPassword();
        String confirmationPassword = passwordUpdate.getNewPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }

    public final void checkOldPassword(UserEntity entity, PasswordUpdate passwordUpdate){
        String oldPassword = passwordUpdate.getOldPassword();
        if(!encoder.matches(oldPassword, entity.getPassword()))
            throw new InvalidOldPasswordException("Wrong old password!");
    }

    public final String encodePassword(String password) {
        return encoder.encode(password);
    }

}
