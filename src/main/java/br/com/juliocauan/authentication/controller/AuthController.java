package br.com.juliocauan.authentication.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.api.AuthApi;
import org.openapitools.model.EnumRole;
import org.openapitools.model.EnumToken;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.security.jwt.TokenUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public final class AuthController implements AuthApi {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryImpl userRepository;
    private final RoleRepositoryImpl roleRepository;
    private final PasswordEncoder encoder;
    private final TokenUtils tokenUtils;

    @Override
    public ResponseEntity<JWTResponse> _signinUser(@Valid SigninForm signinForm) {
        Authentication auth = authenticationManager.authenticate(parseAsToken(signinForm));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = tokenUtils.generateToken(auth);
        UserEntity userEntity = (UserEntity) auth.getPrincipal();		
		List<EnumRole> roles = userEntity.getAuthorities().stream()
			.map(item -> EnumRole.fromValue(item.getAuthority()))
			.collect(Collectors.toList());

        JWTResponse response = new JWTResponse()
            .token(token)
            .type(EnumToken.BEARER)
            .userId(userEntity.getId())
            .username(userEntity.getUsername())
            .email(userEntity.getEmail())
            .roles(roles);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<String> _signupUser(@Valid SignupForm signupForm) {
		if (userRepository.existsByUsername(signupForm.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body("Error: Username is already taken!");
		}

		if (userRepository.existsByEmail(signupForm.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body("Error: Email is already in use!");
		}

		// Create new user's account
        UserEntity userEntity = UserEntity.builder()
            .username(signupForm.getUsername())
            .email(signupForm.getEmail())
            .password(encoder.encode(signupForm.getPassword()))
            .build();
        
		Set<EnumRole> strRoles = signupForm.getRoles();
		Set<RoleEntity> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(EnumRole.USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(RoleEntity.builder().name(userRole.getName()).build());
			
		} else {
			strRoles.forEach(role -> {
				if (role.equals(EnumRole.ADMIN)) {
					Role adminRole = roleRepository.findByName(EnumRole.ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(RoleEntity.builder().name(adminRole.getName()).build());
				} else if (role.equals(EnumRole.MODERATOR)) {
					Role modRole = roleRepository.findByName(EnumRole.MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(RoleEntity.builder().name(modRole.getName()).build());
				} else {
					Role userRole = roleRepository.findByName(EnumRole.USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(RoleEntity.builder().name(userRole.getName()).build());
				}
			});
		}

		
		userEntity.setRoles(roles);
		userRepository.save(userEntity);

		return ResponseEntity.status(HttpStatus.OK).body("User registered successfully!");
    }

    private UsernamePasswordAuthenticationToken parseAsToken(@Valid SigninForm signinForm) {
        return new UsernamePasswordAuthenticationToken(signinForm.getUsername(), signinForm.getPassword());
    }
    
}
