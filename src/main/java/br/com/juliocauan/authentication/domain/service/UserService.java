package br.com.juliocauan.authentication.domain.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.util.PasswordUtil;
import jakarta.persistence.EntityExistsException;

public abstract class UserService {

	protected abstract UserRepository getRepository();
	protected abstract RoleService getRoleService();

	public final User getByUsername(String username) {
		return getRepository().getByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("Username [%s] not found!", username)));
	}

	public final List<UserInfo> getUserInfos(String usernameContains, String roleName) {
		return getRepository().getAll(usernameContains, roleName).stream()
				.map(UserMapper::domainToUserInfo)
				.collect(Collectors.toList());
	}

	public final void register(User user) {
		validateDuplicate(user.getUsername());
		PasswordUtil.validateSecurity(user.getPassword());
		user = User.changePassword(user, PasswordUtil.encode(user.getPassword()));
		getRepository().register(user);
	}

	public final void delete(String username) {
		this.delete(this.getByUsername(username));
	}

	public final void delete(User user) {
		getRepository().delete(user);
	}

	public final void updatePassword(String username, String newPassword) {
		User user = getByUsername(username);
		PasswordUtil.validateSecurity(newPassword);
		user = User.changePassword(user, PasswordUtil.encode(newPassword));
		getRepository().register(user);
	}

	public final void updateRoles(String username, Set<String> roleNames) {
        User user = getByUsername(username);
        Set<Role> roles = roleNames.stream()
            .map(getRoleService()::getByName)
            .collect(Collectors.toSet());
        
        user = User.changeRoles(user, roles);
        getRepository().register(user);
	}

	private final void validateDuplicate(String username) {
		if (getRepository().getByUsername(username).isPresent())
			throw new EntityExistsException(String.format("Username [%s] is already taken!", username));
	}

}
