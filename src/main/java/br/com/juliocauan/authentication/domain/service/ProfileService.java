package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.PasswordUpdateForm;
import org.openapitools.model.Profile;

public interface ProfileService {
    Profile getProfileContent();
    void alterPassword(PasswordUpdateForm passwordUpdateForm);
}
