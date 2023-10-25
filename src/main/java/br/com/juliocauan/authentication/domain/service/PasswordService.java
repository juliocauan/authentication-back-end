package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.NewPasswordForm;
import org.openapitools.model.PasswordUpdateForm;

import br.com.juliocauan.authentication.infrastructure.model.UserEntity;

//TODO review class
public interface PasswordService {
    void checkPasswordConfirmation(NewPasswordForm newPasswordForm);
    void checkPasswordConfirmation(PasswordUpdateForm passwordUpdateForm);
    void checkOldPassword(UserEntity entity, PasswordUpdateForm passwordUpdateForm);
    String encodePassword(String password);
}
