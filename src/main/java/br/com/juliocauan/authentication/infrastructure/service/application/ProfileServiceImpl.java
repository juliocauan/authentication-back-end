package br.com.juliocauan.authentication.infrastructure.service.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.ProfileService;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class ProfileServiceImpl extends ProfileService {

    private final UserRepository userRepository;

    @Override
    protected final UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    protected final String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
