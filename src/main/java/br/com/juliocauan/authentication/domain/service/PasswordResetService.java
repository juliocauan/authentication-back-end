package br.com.juliocauan.authentication.domain.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordReset;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.util.EmailUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;

    public final void sendNewToken(String username) {
        User user = userRepository.findByUsername(username);
        String token = passwordResetRepository.register(user).getToken();
        EmailUtil.sendEmail(
                username,
                "Reset your password!",
                getEmailTemplate(token));
    }

    private final String getEmailTemplate(String token) {
        return "To reset your password, use the following token: %s %n%n This token will last %d minutes".formatted(
                token, PasswordReset.TOKEN_EXPIRATION_MINUTES);
    }

    public final void resetPassword(String newPassword, String token) {
        PasswordReset passwordReset = passwordResetRepository.findByToken(token);
        userRepository.updatePassword(passwordReset.getUser(), newPassword);
        passwordResetRepository.delete(passwordReset);
    }

}
