package br.com.juliocauan.authentication.domain.service;

import java.util.Optional;

import org.openapitools.model.PasswordMatch;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetException;
import br.com.juliocauan.authentication.util.PasswordUtil;
import jakarta.persistence.EntityNotFoundException;

public abstract class PasswordResetService {

    protected abstract PasswordResetRepository getRepository();
    protected abstract UserService getUserService();

    public final String generateToken(String username) {
        User user = getUserService().getByUsername(username);
        deletePreviousPasswordReset(user);
        return getRepository().register(user).getToken();
    }
    
    private final void deletePreviousPasswordReset(User user) {
        Optional<PasswordReset> oldToken = getRepository().getByUser(user);
        if(oldToken.isPresent())
            getRepository().delete(oldToken.get());
    }
    
    public final String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordReset.TOKEN_EXPIRATION_MINUTES);    
    }

    public final void resetPassword(PasswordMatch passwordMatch, String token) {
        PasswordUtil.validateMatch(passwordMatch);
        PasswordReset passwordResetToken = checkTokenValidation(token);
        getUserService().updatePassword(passwordResetToken.getUser().getUsername(), passwordMatch.getPassword());
        getRepository().delete(passwordResetToken);
    }
    
    private final PasswordReset checkTokenValidation(String token) {
        PasswordReset passwordResetToken = getByToken(token);
        if(passwordResetToken.isExpired()){
            getRepository().delete(passwordResetToken);
            throw new ExpiredPasswordResetException("Expired Token!");
        }
        return passwordResetToken;
    }

    private final PasswordReset getByToken(String token) {
        return getRepository().getByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Token [%s] not found!".formatted(token)));
    }

}
