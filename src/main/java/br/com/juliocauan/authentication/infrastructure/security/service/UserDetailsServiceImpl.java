package br.com.juliocauan.authentication.infrastructure.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.util.mapper.UserMapper;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        return UserMapper.domainToUserDetails(userService.findByUsername(username));
    }
    
}