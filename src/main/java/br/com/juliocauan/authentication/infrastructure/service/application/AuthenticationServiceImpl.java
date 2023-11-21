package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.HashSet;
import java.util.Set;

import org.openapitools.model.EnumRole;
import org.openapitools.model.JWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.service.application.AuthenticationService;
import br.com.juliocauan.authentication.domain.service.util.PasswordService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AuthenticationServiceImpl extends AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final PasswordService passwordService;
  private final UserServiceImpl userService;
  private final RoleServiceImpl roleService;

  @Override
  public final JWT authenticate(String username, String password) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
    return new JWT().token(jwtProvider.generateToken(auth));
  }

  @Override
  public final void registerUser(String username, String password, EnumRole role) {
    userService.checkDuplicatedUsername(username);
    userService.save(UserEntity
      .builder()
        .id(null)
        .username(username)
        .password(passwordService.encode(password))
        .roles(buildRoleSet(role))
      .build());
  }

  private final Set<RoleEntity> buildRoleSet(EnumRole formRole) {
    Role role = roleService.getByName(formRole == null ? EnumRole.USER : formRole);
    Set<RoleEntity> roleSet = new HashSet<>();
    roleSet.add(new RoleEntity(role));
    return roleSet;
  }

}
