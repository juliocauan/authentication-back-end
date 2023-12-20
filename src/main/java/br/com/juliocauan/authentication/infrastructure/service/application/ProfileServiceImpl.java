package br.com.juliocauan.authentication.infrastructure.service.application;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.domain.service.application.ProfileService;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class ProfileServiceImpl extends ProfileService {

    private final UserServiceImpl userService;

    @Override
    protected final UserService getUserService() {
        return userService;
    }

    @Override
    protected final String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
