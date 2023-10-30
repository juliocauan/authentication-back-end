package br.com.juliocauan.authentication.infrastructure.service;

import java.util.Optional;

import org.openapitools.model.PasswordMatch;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.PasswordResetTokenService;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredPasswordResetTokenException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.EmailService;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordResetTokenServiceImpl extends PasswordResetTokenService {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;
    private final PasswordService passwordService;

    @Override
    public final String buildTokenAndSendEmail(String username) {
        User user = userService.getByUsername(username);
        String token = createPasswordResetToken(user);
        sendEmail(username, token);
        return token;
    }

    @Override
    public final void resetPassword(PasswordMatch passwordMatch, String token) {
        passwordService.checkPasswordConfirmation(passwordMatch);
        PasswordResetToken passwordResetToken = checkToken(token);
        updateUserPassword(passwordResetToken.getUser(), passwordMatch.getPassword());
        passwordResetTokenRepository.deleteById(passwordResetToken.getId());
    }
    
    private final void deletePreviousPasswordResetToken(User user) {
        Optional<PasswordResetToken> oldToken = passwordResetTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            passwordResetTokenRepository.deleteById(oldToken.get().getId());
    }

    private final String createPasswordResetToken(User user) {
        deletePreviousPasswordResetToken(user);
        return passwordResetTokenRepository.save(new PasswordResetTokenEntity(user)).getToken();
    }

    private final String buildEmailBody(String token) {
        return String.format("To reset your password, use the following token: %s %n%n This token will last %d minutes",
            token, PasswordResetToken.TOKEN_EXPIRATION_MINUTES);    
    }

    private final void sendEmail(String username, String token) {
        emailService.sendEmail(
            username, 
            "Reset your password!", 
            buildEmailBody(token));
    }

    private final PasswordResetToken checkToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Password Reset Token not found with token: " + token));
        if(passwordResetToken.isExpired())
            throw new ExpiredPasswordResetTokenException("Expired Password Reset Token!");
        return passwordResetToken;
    }

    private final void updateUserPassword(User user, String newPassword) {
        UserEntity userEntity = new UserEntity(user);
        userEntity.setPassword(passwordService.encode(newPassword));
        userService.save(userEntity);
    }
}
