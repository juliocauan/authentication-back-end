package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.repository.PasswordResetTokenRepository;
import br.com.juliocauan.authentication.domain.service.PasswordResetTokenService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetTokenRepositoryImpl;
import br.com.juliocauan.authentication.util.EmailService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordResetTokenServiceImpl extends PasswordResetTokenService {

    private final PasswordResetTokenRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;
    private final EmailService emailService;

    @Override
    protected final UserService getUserService() {
        return userService;
    }

    @Override
    protected final PasswordResetTokenRepository getRepository() {
        return passwordResetTokenRepository;
    }

    @Override
    protected final EmailService getEmailService() {
        return emailService;
    }
    
}
