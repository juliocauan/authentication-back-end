package br.com.juliocauan.authentication.infrastructure.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.openapitools.model.NewPasswordForm;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.service.RecoveryTokenService;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredRecoveryTokenException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RecoveryTokenServiceImpl implements RecoveryTokenService {

    private static final int TOKEN_LENGTH = 32;

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;
    private final PasswordServiceImpl passwordService;

    @Override
    public void generateLinkAndSendEmail(String username) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(username));
        sendEmail(createRecoveryToken(user));
    }

    @Override
    public void resetPassword(NewPasswordForm newPasswordForm, String token) {
        passwordService.checkPasswordConfirmation(newPasswordForm);
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Recovery token not found with token: " + token));
        if(passwordResetToken.isExpired())
            throw new ExpiredRecoveryTokenException("Expired recovery token!");
        UserEntity user = UserMapper.domainToEntity(passwordResetToken.getUser());
        user.setPassword(newPasswordForm.getNewPasswordMatch().getPassword());
        userService.save(user);
        passwordResetTokenRepository.deleteById(passwordResetToken.getId());
    }

    private PasswordResetTokenEntity createRecoveryToken(UserEntity user) {
        deletePreviousRecoveryToken(user);
        return passwordResetTokenRepository.save(
            PasswordResetTokenEntity.builder()
                .user(user)
                .token(generateToken())
                .expireDate(LocalDateTime.now().plusMinutes(PasswordResetToken.TOKEN_EXPIRATION_MINUTES))
            .build());
    }

    private void deletePreviousRecoveryToken(UserEntity user) {
        Optional<PasswordResetToken> oldToken = passwordResetTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            passwordResetTokenRepository.deleteById(oldToken.get().getId());
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
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
