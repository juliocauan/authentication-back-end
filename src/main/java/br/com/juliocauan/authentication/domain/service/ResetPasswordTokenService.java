package br.com.juliocauan.authentication.domain.service;

import br.com.juliocauan.authentication.domain.repository.ResetPasswordTokenRepository;

//TODO review
public interface ResetPasswordTokenService {
    
    ResetPasswordTokenRepository getRepository();

}
