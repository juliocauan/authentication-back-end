package br.com.juliocauan.authentication.domain.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;

public abstract class UserService {

	protected abstract UserRepository getRepository();

	public final User getByUsername(String username) {
		return getRepository().findByUsername(username);
	}

	public final List<User> getUsers(String usernameContains, String roleName, Pageable pageable) {
		return getRepository().findAllByFilters(usernameContains, roleName, pageable);
	}

    public List<User> getAllUsers(String roleName) {
        return getRepository().findAllByRole(roleName);
    }

	public final void register(User user) {
		getRepository().register(user);
	}

	public final void update(User user) {
		getRepository().updateRole(user);
	}

	public final void delete(User user) {
		getRepository().delete(user);
	}

	public final void delete(String username) {
		getRepository().delete(username);
	}

	public final void updatePassword(User user, String newPassword) {
		getRepository().updatePassword(user, newPassword);
	}

}
