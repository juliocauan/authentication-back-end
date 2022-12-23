package br.com.juliocauan.authentication.domain.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserService {
    User loadUserByUsername(String username) throws UsernameNotFoundException;
}
