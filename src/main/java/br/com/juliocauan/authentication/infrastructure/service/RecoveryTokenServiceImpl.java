package br.com.juliocauan.authentication.infrastructure.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.openapitools.model.PasswordLinkUpdate;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.RecoveryToken;
import br.com.juliocauan.authentication.domain.service.RecoveryTokenService;
import br.com.juliocauan.authentication.infrastructure.exception.ExpiredRecoveryTokenException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordConfirmationException;
import br.com.juliocauan.authentication.infrastructure.model.RecoveryTokenEntity;
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

    @Override
    public void generateLinkAndSendEmail(String username) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(username));
        sendEmail(createRecoveryToken(user));
    }

    @Override
    public void resetPassword(PasswordLinkUpdate passwordUpdate, String token) {
        checkPasswordConfirmation(passwordUpdate);
        RecoveryToken recoveryToken = recoveryTokenRepository.findByToken(token)
            .orElseThrow(() -> new EntityNotFoundException("Recovery token not found with token: " + token));
        if(recoveryToken.isExpired())
            throw new ExpiredRecoveryTokenException("Expired recovery token!");
        UserEntity user = UserMapper.domainToEntity(recoveryToken.getUser());
        user.setPassword(passwordUpdate.getNewPassword());
        userService.save(user);
        recoveryTokenRepository.deleteById(recoveryToken.getId());
    }

    private RecoveryTokenEntity createRecoveryToken(UserEntity user) {
        deletePreviousRecoveryToken(user);
        return recoveryTokenRepository.save(
            RecoveryTokenEntity.builder()
                .user(user)
                .token(generateToken())
                .expireDate(LocalDateTime.now().plusMinutes(RecoveryToken.EXPIRE))
            .build());
    }

    private void deletePreviousRecoveryToken(UserEntity user) {
        Optional<RecoveryToken> oldToken = recoveryTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            recoveryTokenRepository.deleteById(oldToken.get().getId());
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void sendEmail(RecoveryTokenEntity resetToken) {
        //TODO review this URL
        String url = "localhost:4200/forgotPassword/";
        String message = String.format("To reset your password, click on the following link: %s%s %n%n This link will last %d minutes",
            url, resetToken.getToken(), RecoveryToken.EXPIRE);
        
        emailService.sendEmail(
            resetToken.getUser().getUsername(), 
            "Reset your password!", 
            message);
    }


    private void checkPasswordConfirmation(PasswordLinkUpdate passwordUpdate){
        String newPassword = passwordUpdate.getNewPassword();
        String confirmationPassword = passwordUpdate.getNewPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }

}
