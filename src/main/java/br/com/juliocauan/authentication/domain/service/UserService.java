package br.com.juliocauan.authentication.domain.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.openapitools.model.UserInfo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import jakarta.persistence.EntityExistsException;

public abstract class UserService {
	
	protected abstract UserRepository getRepository();

	public abstract User save(User user);
    public abstract void updatePassword(User user, String encodedPassword);
    public abstract void updateRoles(String username, Set<EnumRole> enumRoles);

    public final User getByUsername(String username){
        return getRepository().getByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
	}

	public final void checkDuplicatedUsername(String username) {
		if (getRepository().existsByUsername(username))
			throw new EntityExistsException("Username is already taken!");
    }

	public final List<UserInfo> getUserInfosByUsernameSubstringAndRole(String username, EnumRole role){
		return getRepository().getAllByUsernameSubstringAndRole(username, role).stream()
            .map(UserMapper::domainToUserInfo)
            .collect(Collectors.toList());
	}
	
}
