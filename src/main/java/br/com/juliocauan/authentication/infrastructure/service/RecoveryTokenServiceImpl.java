package br.com.juliocauan.authentication.infrastructure.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.openapitools.model.PasswordLinkUpdate;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.service.RecoveryTokenService;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredRecoveryTokenException;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RecoveryTokenRepositoryImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RecoveryTokenServiceImpl implements RecoveryTokenService {

    private static final int TOKEN_LENGTH = 32;

    private final RecoveryTokenRepositoryImpl recoveryTokenRepository;
    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;
    private final PasswordServiceImpl passwordService;

    @Override
    public void generateLinkAndSendEmail(String username) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(username));
        sendEmail(createRecoveryToken(user));
    }

    @Override
    public void resetPassword(PasswordLinkUpdate passwordUpdate, String token) {
        passwordService.checkPasswordConfirmation(passwordUpdate);
        PasswordResetToken recoveryToken = recoveryTokenRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Recovery token not found with token: " + token));
        if(recoveryToken.isExpired())
            throw new ExpiredRecoveryTokenException("Expired recovery token!");
        UserEntity user = UserMapper.domainToEntity(recoveryToken.getUser());
        user.setPassword(passwordUpdate.getNewPassword());
        userService.save(user);
        recoveryTokenRepository.deleteById(recoveryToken.getId());
    }

    private PasswordResetTokenEntity createRecoveryToken(UserEntity user) {
        deletePreviousRecoveryToken(user);
        return recoveryTokenRepository.save(
            PasswordResetTokenEntity.builder()
                .user(user)
                .token(generateToken())
                .expireDate(LocalDateTime.now().plusMinutes(PasswordResetToken.TOKEN_EXPIRATION_MINUTES))
            .build());
    }

    private void deletePreviousRecoveryToken(UserEntity user) {
        Optional<PasswordResetToken> oldToken = recoveryTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            recoveryTokenRepository.deleteById(oldToken.get().getId());
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
