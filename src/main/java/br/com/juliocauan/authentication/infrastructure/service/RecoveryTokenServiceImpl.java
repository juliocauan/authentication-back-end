package br.com.juliocauan.authentication.infrastructure.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.RecoveryToken;
import br.com.juliocauan.authentication.domain.service.RecoveryTokenService;
import br.com.juliocauan.authentication.infrastructure.model.RecoveryTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RecoveryTokenRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RecoveryTokenServiceImpl implements RecoveryTokenService {

    private static final int TOKEN_LENGTH = 32;
    private static final int EXPIRE = 30;

    private final RecoveryTokenRepositoryImpl recoveryTokenRepository;
    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;

    @Override
    public void generateLinkAndSendEmail(String username) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(username));
        sendEmail(createRecoveryToken(user));
    }

    private RecoveryTokenEntity createRecoveryToken(UserEntity user) {
        deletePreviousRecoveryToken(user);
        return recoveryTokenRepository.save(
            RecoveryTokenEntity.builder()
                .user(user)
                .token(generateToken())
                .expireDate(LocalDateTime.now().plusMinutes(EXPIRE))
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
            url, resetToken.getToken(), EXPIRE);
        
        emailService.sendEmail(
            resetToken.getUser().getUsername(), 
            "Reset your password!", 
            message);
    }

}
