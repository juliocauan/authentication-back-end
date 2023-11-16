package br.com.juliocauan.authentication.domain.service;

import java.util.Optional;

import org.openapitools.model.PasswordMatch;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.PasswordResetTokenRepository;
import br.com.juliocauan.authentication.domain.service.util.EmailService;
import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetTokenException;
import jakarta.persistence.EntityNotFoundException;

public abstract class PasswordResetTokenService {

    protected abstract UserService getUserService();
    protected abstract PasswordResetTokenRepository getRepository();
    protected abstract EmailService getEmailService();
    protected abstract PasswordService getPasswordService();

    protected abstract PasswordResetToken saveWithUser(User user);

    public final String generatePasswordResetToken(String username) {
        User user = getUserService().getByUsername(username);
        deleteUserPreviousPasswordResetToken(user);
        return this.saveWithUser(user).getToken();
    }
    
    private final void deleteUserPreviousPasswordResetToken(User user) {
        Optional<PasswordResetToken> oldToken = getRepository().getByUser(user);
        if(oldToken.isPresent())
            getRepository().deleteById(oldToken.get().getId());
    }

    public final void sendEmail(String username, String token) {
        getEmailService().sendEmail(
            username, 
            "Reset your password!", 
            buildEmailBody(token));
    }   
    
    private final String buildEmailBody(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordResetToken.TOKEN_EXPIRATION_MINUTES);    
    }

    public final void resetPassword(PasswordMatch passwordMatch, String token) {
        getPasswordService().checkPasswordConfirmation(passwordMatch);
        PasswordResetToken passwordResetToken = checkTokenValidation(token);
        updateUserPassword(passwordResetToken.getUser(), passwordMatch.getPassword());
        getRepository().deleteById(passwordResetToken.getId());
    }
    
    private final PasswordResetToken checkTokenValidation(String token) {
        PasswordResetToken passwordResetToken = getByToken(token);
        if(passwordResetToken.isExpired())
            throw new ExpiredPasswordResetTokenException("Expired Password Reset Token!");
        return passwordResetToken;
    }

    private final PasswordResetToken getByToken(String token) {
        return getRepository().getByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Password Reset Token not found with token: " + token));
    }
    
    private final void updateUserPassword(User user, String newPassword) {
        String encodedPassword = getPasswordService().encode(newPassword);
        getUserService().updatePassword(user, encodedPassword);
    }

}
