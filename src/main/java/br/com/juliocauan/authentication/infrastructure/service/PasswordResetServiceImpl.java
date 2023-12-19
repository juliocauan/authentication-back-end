package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.domain.service.PasswordResetService;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordResetServiceImpl extends PasswordResetService {

    private final PasswordResetRepositoryImpl passwordResetTokenRepository;
    private final UserServiceImpl userService;

    @Override
    protected final UserService getUserService() {
        return userService;
    }

    @Override
    protected final PasswordResetRepository getRepository() {
        return passwordResetTokenRepository;
    }
    
}
