package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.PasswordResetToken;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.PasswordResetTokenRepository;
import br.com.juliocauan.authentication.domain.service.PasswordResetTokenService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.model.PasswordResetTokenEntity;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.EmailService;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordResetTokenServiceImpl extends PasswordResetTokenService {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;
    private final PasswordService passwordService;

    @Override
    protected final UserService getUserService() {
        return userService;
    }

    @Override
    protected final PasswordResetTokenRepository getRepository() {
        return passwordResetTokenRepository;
    }

    @Override
    protected final PasswordResetToken saveWithUser(User user) {
        return passwordResetTokenRepository.save(new PasswordResetTokenEntity(user));
    }

    @Override
    protected EmailService getEmailService() {
        return emailService;
    }

    @Override
    protected PasswordService getPasswordService() {
        return passwordService;
    }
}
