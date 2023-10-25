package br.com.juliocauan.authentication.infrastructure.service;

import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.domain.service.UserService;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class UserServiceImpl extends UserService {
    
    private final UserRepositoryImpl userRepository;

    @Override
    public final User save(User user) {
        return userRepository.save(UserMapper.domainToEntity(user));
    }

    @Override
    protected final UserRepository getRepository() {
        return userRepository;
    }
    
}
