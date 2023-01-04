package br.com.juliocauan.authentication.domain.service;

import br.com.juliocauan.authentication.domain.model.User;

public interface UserService {
    User findByUsername(String username);
	void checkDuplicatedUsername(String username);
	void checkDuplicatedEmail(String email);
	void save(User user);
}
