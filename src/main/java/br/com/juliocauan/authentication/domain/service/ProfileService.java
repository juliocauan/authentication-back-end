package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.PasswordUpdate;
import org.openapitools.model.Profile;

public interface ProfileService {
    Profile getProfileContent();
    void alterPassword(PasswordUpdate passwordUpdate);
}
