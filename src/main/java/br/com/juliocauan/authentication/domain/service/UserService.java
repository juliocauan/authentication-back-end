package br.com.juliocauan.authentication.domain.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.util.PasswordUtil;
import jakarta.persistence.EntityExistsException;

public abstract class UserService {

	protected abstract UserRepository getRepository();

	public final User getByUsername(String username) {
		return getRepository().getByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(exceptionNotFound(username)));
	}

	private final String exceptionNotFound(String username) {
		return "Username [%s] not found!".formatted(username);
	}

	public final List<User> getUsers(String usernameContains, String roleName, Pageable pageable) {
		return getRepository().getAll(usernameContains, roleName, pageable);
	}

    public List<User> getAllUsers(String roleName) {
        return getRepository().getAll(roleName);
    }

	public final void register(User user) {
		validateDuplicate(user.getUsername());
		user = setNewEncodedPassword(user, user.getPassword());
		getRepository().register(user);
	}

	private final void validateDuplicate(String username) {
		if(getRepository().getByUsername(username).isPresent())
			throw new EntityExistsException(exceptionDuplicated(username));
	}

	private final String exceptionDuplicated(String username) {
		return "Username [%s] is already taken!".formatted(username);
	}

	private final User setNewEncodedPassword(User user, String password) {
		PasswordUtil.validateSecurity(password);
		return User.changePassword(user, PasswordUtil.encode(password));
	}

	public final void update(User user) {
		getRepository().register(user);
	}

	public final void delete(User user) {
		getRepository().delete(user);
	}

	public final void delete(String username) {
		delete(getByUsername(username));
	}

	public final void updatePassword(User user, String newPassword) {
		user = setNewEncodedPassword(user, newPassword);
		getRepository().register(user);
	}

}
