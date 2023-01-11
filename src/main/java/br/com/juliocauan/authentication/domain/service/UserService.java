package br.com.juliocauan.authentication.domain.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import jakarta.persistence.EntityExistsException;

public interface UserService {
	
	UserRepository getRepository();
	void save(User user);

    default User getByUsername(String username){
        return getRepository().findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
	}

	default void checkDuplicatedUsername(String username) {
		if (getRepository().existsByUsername(username))
			throw new EntityExistsException("Username is already taken!");
    }

	default void checkDuplicatedEmail(String email) {
		if (getRepository().existsByEmail(email))
			throw new EntityExistsException("Email is already in use!");
    }
	
}
