package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }

    @Override
    public void checkDuplicatedUsername(String username) {
		if (userRepository.existsByUsername(username))
			throw new EntityExistsException("Error: Username is already taken!");
    }

    @Override
    public void checkDuplicatedEmail(String email) {
		if (userRepository.existsByEmail(email))
			throw new EntityExistsException("Error: Email is already in use!");
    }

    @Override
    public void save(User user) {
        userRepository.save(UserMapper.domainToEntity(user));
    }
    
}
