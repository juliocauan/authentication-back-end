package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.NewPasswordForm;

public abstract class PasswordResetTokenService {
    public abstract void buildTokenAndSendEmail(String username);
    public abstract void resetPassword(NewPasswordForm newPasswordForm, String token);
}
