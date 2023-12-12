package br.com.juliocauan.authentication.infrastructure.service.util;

import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidPasswordException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordServiceImpl extends PasswordService {

    private final PasswordEncoder encoder;
    private final Environment env;

    @Override
    public final String encode(String password) {
        return encoder.encode(password);
    }

    @Override
    protected final boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public final void validateAdminPassword(String adminPassword) {
        if(!matches(adminPassword, encode(env.getProperty("REGISTER_ADMIN_PASSWORD"))))
            throw new InvalidPasswordException("Admin Password is incorrect!");
    }
    
}
