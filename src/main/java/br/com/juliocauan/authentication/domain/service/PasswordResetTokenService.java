package br.com.juliocauan.authentication.domain.service;

import java.util.Optional;

import org.openapitools.model.PasswordMatch;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.PasswordResetTokenRepository;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetTokenException;
import br.com.juliocauan.authentication.util.PasswordUtil;
import jakarta.persistence.EntityNotFoundException;

public abstract class PasswordResetTokenService {

    protected abstract PasswordResetTokenRepository getRepository();
    protected abstract UserService getUserService();

    public final String generateToken(String username) {
        User user = getUserService().getBy(username);
        deletePreviousPasswordResetToken(user);
        return getRepository().register(user).getToken();
    }
    
    private final void deletePreviousPasswordResetToken(User user) {
        Optional<PasswordResetToken> oldToken = getRepository().getByUser(user);
        if(oldToken.isPresent())
            getRepository().delete(oldToken.get());
    }
    
    public final String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordResetToken.TOKEN_EXPIRATION_MINUTES);    
    }

    public final void resetPassword(PasswordMatch passwordMatch, String token) {
        PasswordUtil.validatePasswordMatch(passwordMatch);
        PasswordResetToken passwordResetToken = checkTokenValidation(token);
        getUserService().updatePassword(passwordResetToken.getUser().getUsername(), passwordMatch.getPassword());
        getRepository().delete(passwordResetToken);
    }
    
    private final PasswordResetToken checkTokenValidation(String token) {
        PasswordResetToken passwordResetToken = getByToken(token);
        if(passwordResetToken.isExpired()){
            getRepository().delete(passwordResetToken);
            throw new ExpiredPasswordResetTokenException("Expired Token!");
        }
        return passwordResetToken;
    }

    private final PasswordResetToken getByToken(String token) {
        return getRepository().getByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Token [%s] not found!".formatted(token)));
    }

}
