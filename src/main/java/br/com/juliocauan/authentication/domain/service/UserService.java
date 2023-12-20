package br.com.juliocauan.authentication.domain.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.util.PasswordUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public abstract class UserService {

	protected abstract UserRepository getRepository();

	public final User getByUsername(String username) {
		return getRepository().getByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(exceptionNotFound(username)));
	}

	private final String exceptionNotFound(String username) {
		return "Username [%s] not found!".formatted(username);
	}

	public final List<User> getUsers(String usernameContains, String roleName) {
		return getRepository().getAll(usernameContains, roleName);
	}

	public final void register(User user) {
		if(isPresent(user.getUsername()))
			throw new EntityExistsException(exceptionDuplicated(user.getUsername()));
		user = setNewEncodedPassword(user, user.getPassword());
		getRepository().register(user);
	}

	private final boolean isPresent(String username) {
		return getRepository().getByUsername(username).isPresent();
	}

	private final String exceptionDuplicated(String username) {
		return "Username [%s] is already taken!".formatted(username);
	}

	private final User setNewEncodedPassword(User user, String password) {
		PasswordUtil.validateSecurity(password);
		return User.changePassword(user, PasswordUtil.encode(password));
	}

	//TODO check this: consults database twice before being this method is called
	public final void update(User user) {
		if(!isPresent(user.getUsername()))
			throw new EntityNotFoundException(exceptionNotFound(user.getUsername()));
		getRepository().register(user);
	}

	//TODO check this: consults database twice before being this method is called
	public final void delete(String username) {
		getRepository().delete(getByUsername(username));
	}

	//TODO check this: consults database twice before being this method is called
	public final void updatePassword(String username, String newPassword) {
		User user = getByUsername(username);
		user = setNewEncodedPassword(user, newPassword);
		getRepository().register(user);
	}

}
