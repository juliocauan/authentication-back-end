package br.com.juliocauan.authentication.infrastructure.service.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class PasswordServiceImpl extends PasswordService {

    private final PasswordEncoder encoder;

    @Override
    public final String encode(String password) {
        return encoder.encode(password);
    }

    @Override
    protected final boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
}
