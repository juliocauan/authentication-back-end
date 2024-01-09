package br.com.juliocauan.authentication.domain.service;

import java.util.Optional;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetException;
import br.com.juliocauan.authentication.util.EmailUtil;
import jakarta.persistence.EntityNotFoundException;

public abstract class PasswordResetService {

    protected abstract PasswordResetRepository getRepository();

    protected abstract UserService getUserService();

    public final void sendNewToken(String username) {
        User user = getUserService().getByUsername(username);
        deletePreviousPasswordReset(user);
        String token = getRepository().register(user).getToken();
        EmailUtil.sendEmail(
                username,
                "Reset your password!",
                getEmailTemplate(token));
    }

    private final void deletePreviousPasswordReset(User user) {
        Optional<PasswordReset> oldToken = getRepository().getByUser(user);
        if (oldToken.isPresent())
            getRepository().delete(oldToken.get());
    }

    private final String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordReset.TOKEN_EXPIRATION_MINUTES);
    }

    public final void resetPassword(String newPassword, String token) {
        PasswordReset passwordReset = checkTokenValidation(token);
        getUserService().updatePassword(passwordReset.getUser(), newPassword);
        getRepository().delete(passwordReset);
    }

    private final PasswordReset checkTokenValidation(String token) {
        PasswordReset passwordReset = getByToken(token);
        if (passwordReset.isExpired()) {
            getRepository().delete(passwordReset);
            throw new ExpiredPasswordResetException("Expired Token!");
        }
        return passwordReset;
    }

    private final PasswordReset getByToken(String token) {
        return getRepository().getByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token [%s] not found!".formatted(token)));
    }

}
