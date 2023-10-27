package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.PasswordMatch;

public abstract class PasswordResetTokenService {
    public abstract String buildTokenAndSendEmail(String username);
    public abstract void resetPassword(PasswordMatch passwordMatch, String token);
}
