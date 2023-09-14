package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.PasswordLinkUpdate;

public interface RecoveryTokenService {
    void generateLinkAndSendEmail(String username);
    void resetPassword(PasswordLinkUpdate passwordUpdate, String token);
}
