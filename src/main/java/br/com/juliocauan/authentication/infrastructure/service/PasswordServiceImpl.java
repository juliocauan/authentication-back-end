package br.com.juliocauan.authentication.infrastructure.service;

import org.openapitools.model.NewPasswordForm;
import org.openapitools.model.PasswordUpdateForm;
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

    public final void checkPasswordConfirmation(NewPasswordForm newPasswordForm){
        String newPassword = newPasswordForm.getNewPasswordMatch().getPassword();
        String confirmationPassword = newPasswordForm.getNewPasswordMatch().getPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }
    
    public final void checkPasswordConfirmation(PasswordUpdateForm passwordUpdateForm){
        String newPassword = passwordUpdateForm.getNewPasswordMatch().getPassword();
        String confirmationPassword = passwordUpdateForm.getNewPasswordMatch().getPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }

    public final void checkOldPassword(UserEntity entity, PasswordUpdateForm passwordUpdateForm){
        String oldPassword = passwordUpdateForm.getOldPassword();
        if(!encoder.matches(oldPassword, entity.getPassword()))
            throw new InvalidOldPasswordException("Wrong old password!");
    }

    public final String encodePassword(String password) {
        return encoder.encode(password);
    }

}
