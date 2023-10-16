package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.PasswordLinkUpdate;
import org.openapitools.model.PasswordUpdate;

import br.com.juliocauan.authentication.infrastructure.model.UserEntity;

//TODO review class
public interface PasswordService {
    void checkPasswordConfirmation(PasswordLinkUpdate passwordUpdate);
    void checkPasswordConfirmation(PasswordUpdate passwordUpdate);
    void checkOldPassword(UserEntity entity, PasswordUpdate passwordUpdate);
    String encodePassword(String password);
}
