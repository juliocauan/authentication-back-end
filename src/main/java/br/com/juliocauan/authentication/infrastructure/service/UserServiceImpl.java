package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    
    private final UserRepositoryImpl userRepository;

    @Override
    public void save(User user) {
        userRepository.save(UserMapper.domainToEntity(user));
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    @Override
    @Transactional
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
		return UserMapper.domainToEntity(getByUsername(username));
    }
    
}
