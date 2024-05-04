package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.PasswordResetService;
import br.com.juliocauan.authentication.infrastructure.repository.PasswordResetRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordResetServiceImpl extends PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;

    @Override
    protected final PasswordResetRepository getRepository() {
        return passwordResetRepository;
    }

    @Override
    protected final UserRepository getUserRepository() {
        return userRepository;
    }
    
}
