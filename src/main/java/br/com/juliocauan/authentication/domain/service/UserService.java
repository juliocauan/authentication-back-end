package br.com.juliocauan.authentication.domain.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import jakarta.persistence.EntityExistsException;

public abstract class UserService {

	protected abstract UserRepository getRepository();

	public abstract void updatePassword(User user, String encodedPassword);

	public abstract void updateRoles(String username, Set<String> roles);

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
		validateUsername(user.getUsername());
		getRepository().register(user);
	}

	public final void delete(String username) {
		this.delete(this.getByUsername(username));
	}

	public final void delete(User user) {
		getRepository().delete(user);
	}

	private final void validateUsername(String username) {
		if (getRepository().existsByUsername(username))
			throw new EntityExistsException(String.format("Username [%s] is already taken!", username));
	}

}
