package br.com.juliomariano.authentication.infrastructure.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliomariano.authentication.domain.service.UserService;
import br.com.juliomariano.authentication.util.mapper.UserMapper;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        return UserMapper.INSTANCE.toUserDetails(userService.findByUsername(username));
    }
    
}