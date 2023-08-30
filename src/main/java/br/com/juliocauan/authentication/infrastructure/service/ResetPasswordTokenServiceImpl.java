package br.com.juliocauan.authentication.infrastructure.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.ResetPasswordToken;
import br.com.juliocauan.authentication.domain.service.ResetPasswordTokenService;
import br.com.juliocauan.authentication.infrastructure.model.ResetPasswordTokenEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.ResetPasswordTokenMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.ResetPasswordTokenRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ResetPasswordTokenServiceImpl implements ResetPasswordTokenService {

    private static final int TOKEN_LENGTH = 32;
    private static final int EXPIRE = 30;

    private final ResetPasswordTokenRepositoryImpl resetPasswordTokenRepository;
    private final UserServiceImpl userService;
    private final JavaMailSender mailSender;

    @Override
    public void generateResetTokenAndSendEmail(String username) {
        UserEntity user = UserMapper.domainToEntity(userService.getByUsername(username));
        String token = generateToken();
        checkAndDeletePreviousResetPasswordToken(user);
        ResetPasswordTokenEntity resetPasswordToken = createResetPasswordToken(user, token);
        sendEmail(resetPasswordToken);
    }

    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void checkAndDeletePreviousResetPasswordToken(UserEntity user) {
        Optional<ResetPasswordToken> oldToken = resetPasswordTokenRepository.findByUser(user);
        if(oldToken.isPresent())
            resetPasswordTokenRepository.delete(ResetPasswordTokenMapper.domainToEntity(oldToken.get()));
    }

    private ResetPasswordTokenEntity createResetPasswordToken(UserEntity user, String token) {
        return resetPasswordTokenRepository.save(
            ResetPasswordTokenEntity.builder()
                .user(user)
                .token(token)
                .expireDate(LocalDateTime.now().plusMinutes(EXPIRE))
            .build());
    }

    private void sendEmail(ResetPasswordTokenEntity resetToken) {
        SimpleMailMessage email = new SimpleMailMessage();
        String url = "localhost:4200/forgotPassword/";
        email.setSubject("Reset your password!");
        email.setTo(resetToken.getUser().getUsername());
        email.setText(String.format("To reset your password, click on the following link: %s%s %n%n This link will last %d minutes",
            url, resetToken.getToken(), EXPIRE));
        mailSender.send(email);
    }

}
