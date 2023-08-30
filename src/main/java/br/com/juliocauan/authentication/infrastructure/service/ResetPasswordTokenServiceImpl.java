package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.repository.ResetPasswordTokenRepository;
import br.com.juliocauan.authentication.domain.service.ResetPasswordTokenService;
import br.com.juliocauan.authentication.infrastructure.repository.ResetPasswordTokenRepositoryImpl;
import lombok.AllArgsConstructor;

//TODO review
@Service
@AllArgsConstructor
public class ResetPasswordTokenServiceImpl implements ResetPasswordTokenService {
    
    private final ResetPasswordTokenRepositoryImpl resetPasswordTokenRepository;

    @Override
    public ResetPasswordTokenRepository getRepository() {
        return resetPasswordTokenRepository;
    }
    
}
