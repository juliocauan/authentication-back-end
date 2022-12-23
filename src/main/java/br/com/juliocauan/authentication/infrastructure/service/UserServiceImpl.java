package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
	UserRepositoryImpl userRepository;

	@Override
	@Transactional
	public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		return UserEntity.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPassword())
        .build();
	}

}
