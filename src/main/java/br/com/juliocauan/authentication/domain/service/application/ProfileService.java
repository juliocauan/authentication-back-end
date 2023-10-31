package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.PasswordUpdateForm;
import org.openapitools.model.Profile;

public abstract class ProfileService {
    public abstract Profile getProfileContent();
    public abstract void updatePassword(PasswordUpdateForm passwordUpdateForm);
}
