package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.PasswordUpdateForm;

public abstract class ProfileService {
    public abstract void updatePassword(PasswordUpdateForm passwordUpdateForm);
}
