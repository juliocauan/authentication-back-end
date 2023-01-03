package br.com.juliocauan.authentication.infrastructure.security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.openapitools.model.EnumToken;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.jwt.TokenUtils;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class TokenService {
    
    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;
    private final PasswordEncoder encoder;
    
    public JWTResponse authenticate(SigninForm signinForm) {
        Authentication auth = authenticationManager.authenticate(parseAsAuthToken(signinForm));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = tokenUtils.generateToken(auth);
        UserEntity userEntity = (UserEntity) auth.getPrincipal();		
		List<EnumRole> roles = userEntity.getAuthorities().stream()
			.map(item -> EnumRole.fromValue(item.getAuthority()))
			.collect(Collectors.toList());
		return parseAsJWTResponse(token, userEntity, roles);
    }

    public void validateAndRegisterNewUser(SignupForm signupForm) {
		if (userRepository.existsByUsername(signupForm.getUsername()))
			throw new EntityExistsException("Error: Username is already taken!");
		if (userRepository.existsByEmail(signupForm.getEmail()))
			throw new EntityExistsException("Error: Email is already in use!");

		UserEntity userEntity = UserMapper.formToEntityWithEncodedPassword(signupForm, encoder);
		Set<RoleEntity> roles = new HashSet<>();
		signupForm.getRoles().forEach(role -> roles.add(RoleMapper.domainToEntity(roleRepository.findByName(role))));
		userEntity.setRoles(roles);
		userRepository.save(userEntity);
    }
    
    private UsernamePasswordAuthenticationToken parseAsAuthToken(SigninForm signinForm) {
        return new UsernamePasswordAuthenticationToken(signinForm.getUsername(), signinForm.getPassword());
    }

    private JWTResponse parseAsJWTResponse(String token, UserEntity userEntity, List<EnumRole> roles) {
		return new JWTResponse()
			.token(token)
			.type(EnumToken.BEARER)
			.userId(userEntity.getId())
			.username(userEntity.getUsername())
			.email(userEntity.getEmail())
			.roles(roles);
	}

}
