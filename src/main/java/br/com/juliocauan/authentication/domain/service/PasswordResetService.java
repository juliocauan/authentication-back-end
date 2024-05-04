package br.com.juliocauan.authentication.domain.service;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.EmailUtil;

public abstract class PasswordResetService {

    protected abstract PasswordResetRepository getRepository();

    protected abstract UserRepository getUserRepository();

    public final void sendNewToken(String username) {
        User user = getUserRepository().findByUsername(username);
        String token = getRepository().register(user).getToken();
        EmailUtil.sendEmail(
                username,
                "Reset your password!",
                getEmailTemplate(token));
    }

    private final String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordReset.TOKEN_EXPIRATION_MINUTES);
    }

    public final void resetPassword(String newPassword, String token) {
        PasswordReset passwordReset = getRepository().findByToken(token);
        getUserRepository().updatePassword(passwordReset.getUser(), newPassword);
        getRepository().delete(passwordReset);
    }

}
