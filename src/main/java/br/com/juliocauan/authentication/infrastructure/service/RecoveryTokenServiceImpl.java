package br.com.juliocauan.authentication.infrastructure.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.RecoveryToken;
import br.com.juliocauan.authentication.domain.service.RecoveryTokenService;
import br.com.juliocauan.authentication.infrastructure.model.RecoveryTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RecoveryTokenMapper;
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
    private final JavaMailSender mailSender;

    @Override
    public void generateLinkAndSendEmail(String username) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(username));
        String token = generateToken();
        RecoveryTokenEntity recoveryToken = createRecoveryToken(user, token);
        sendEmail(recoveryToken);
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void deletePreviousRecoveryToken(UserEntity user) {
        Optional<RecoveryToken> oldToken = recoveryTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            recoveryTokenRepository.delete(RecoveryTokenMapper.domainToEntity(oldToken.get()));
    }

    private RecoveryTokenEntity createRecoveryToken(UserEntity user, String token) {
        deletePreviousRecoveryToken(user);
        return recoveryTokenRepository.save(
            RecoveryTokenEntity.builder()
                .user(user)
                .token(token)
                .expireDate(LocalDateTime.now().plusMinutes(EXPIRE))
            .build());
    }

    private void sendEmail(RecoveryTokenEntity resetToken) {
        SimpleMailMessage email = new SimpleMailMessage();
        String url = "localhost:4200/forgotPassword/";
        email.setSubject("Reset your password!");
        email.setTo(resetToken.getUser().getUsername());
        email.setText(String.format("To reset your password, click on the following link: %s%s %n%n This link will last %d minutes",
            url, resetToken.getToken(), EXPIRE));
        mailSender.send(email);
    }

}
