package br.com.juliocauan.authentication.infrastructure.service;

import java.util.Optional;

import org.openapitools.model.NewPasswordForm;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.service.PasswordResetTokenService;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredRecoveryTokenException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.EmailService;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;
    private final PasswordService passwordService;

    @Override
    public void generateLinkAndSendEmail(String username) {
        UserEntity user = new UserEntity(userService.getByUsername(username));
        sendEmail(createRecoveryToken(user));
    }

    @Override
    public void resetPassword(NewPasswordForm newPasswordForm, String token) {
        passwordService.checkPasswordConfirmation(newPasswordForm.getNewPasswordMatch());
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Recovery token not found with token: " + token));
        if(passwordResetToken.isExpired())
            throw new ExpiredRecoveryTokenException("Expired recovery token!");
        UserEntity user = new UserEntity(passwordResetToken.getUser());
        user.setPassword(newPasswordForm.getNewPasswordMatch().getPassword());
        userService.save(user);
        passwordResetTokenRepository.deleteById(passwordResetToken.getId());
    }

    private PasswordResetTokenEntity createRecoveryToken(UserEntity user) {
        deletePreviousRecoveryToken(user);
        return passwordResetTokenRepository.save(new PasswordResetTokenEntity(user));
    }

    private void deletePreviousRecoveryToken(UserEntity user) {
        Optional<PasswordResetToken> oldToken = passwordResetTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            passwordResetTokenRepository.deleteById(oldToken.get().getId());
    }

    private void sendEmail(PasswordResetTokenEntity resetToken) {
        emailService.sendEmail(
            resetToken.getUser().getUsername(), 
            "Reset your password!", 
            emailBody(resetToken.getToken()));
    }

    private String emailBody(String token) {
        //TODO review this URL
        String url = "http://localhost:4200/forgotPassword/";
        return String.format("To reset your password, click on the following link: %s%s %n%n This link will last %d minutes",
            url, token, PasswordResetToken.TOKEN_EXPIRATION_MINUTES);    
    }

}
