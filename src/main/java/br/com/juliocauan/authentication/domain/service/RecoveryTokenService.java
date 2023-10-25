package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.NewPasswordForm;

public interface RecoveryTokenService {
    void generateLinkAndSendEmail(String username);
    void resetPassword(NewPasswordForm newPasswordForm, String token);
}
