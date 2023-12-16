package br.com.juliocauan.authentication.domain.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.util.PasswordUtil;
import br.com.juliocauan.authentication.util.UserMapper;
import jakarta.persistence.EntityExistsException;

public abstract class UserService {

	protected abstract UserRepository getRepository();

	protected abstract RoleService getRoleService();

	public final User getBy(String username) {
		return getRepository().getByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("Username [%s] not found!", username)));
	}

	public final List<UserInfo> getUserInfos(String usernameContains, String roleName) {
		return getRepository().getAll(usernameContains, roleName).stream()
				.map(UserMapper::domainToUserInfo)
				.collect(Collectors.toList());
	}

	public final void registerNew(User user) {
		checkForDuplicate(user.getUsername());
		user = setNewEncodedPassword(user, user.getPassword());
		getRepository().register(user);
	}

	private final void checkForDuplicate(String username) {
		if (getRepository().getByUsername(username).isPresent())
			throw new EntityExistsException(String.format("Username [%s] is already taken!", username));
	}

	private final User setNewEncodedPassword(User user, String password) {
		PasswordUtil.validateSecurity(password);
		return User.changePassword(user, PasswordUtil.encode(password));
	}

	public final void delete(String username) {
		getRepository().delete(getBy(username));
	}

	public final void updatePassword(String username, String newPassword) {
		User user = getBy(username);
		user = setNewEncodedPassword(user, newPassword);
		getRepository().register(user);
	}

	public final void updateRoles(String username, Set<String> roleNames) {
		User user = getBy(username);
		Set<Role> roles = roleNames.stream()
				.map(getRoleService()::getByName)
				.collect(Collectors.toSet());

		user = User.changeRoles(user, roles);
		getRepository().register(user);
	}

}
