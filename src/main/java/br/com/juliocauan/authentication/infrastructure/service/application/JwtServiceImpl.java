package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.HashSet;
import java.util.Set;

import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.service.application.JwtService;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.util.PasswordService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class JwtServiceImpl extends JwtService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final PasswordService passwordService;
  private final UserServiceImpl userService;
  private final RoleServiceImpl roleService;

  @Override
  public final JWTResponse authenticate(SigninForm signinForm) {
    Authentication auth = authenticationManager.authenticate(parseAsAuthToken(signinForm));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String token = jwtProvider.generateToken(auth);
    UserDetails userPrincipal = (UserDetails) auth.getPrincipal();
    Set<EnumRole> roles = RoleMapper.authoritiesToEnumRole(userPrincipal.getAuthorities());
    return parseAsJWTResponse(token, userPrincipal, roles);
  }

  @Override
  public final void validateAndRegisterNewUser(String username, String password, EnumRole role) {
    userService.checkDuplicatedUsername(username);
    UserEntity userEntity = buildUserEntity(username, password, role);
    userService.save(userEntity);
  }

  private final UsernamePasswordAuthenticationToken parseAsAuthToken(SigninForm signinForm) {
    return new UsernamePasswordAuthenticationToken(signinForm.getUsername(), signinForm.getPassword());
  }

  private final JWTResponse parseAsJWTResponse(String token, UserDetails userPrincipal, Set<EnumRole> roles) {
    return new JWTResponse()
        .token(token)
        .username(userPrincipal.getUsername())
        .roles(roles);
  }

  private final UserEntity buildUserEntity(String username, String password, EnumRole role) {
    return UserEntity.builder()
      .id(null)
      .username(username)
      .password(passwordService.encode(password))
      .roles(buildRoleSet(role))
    .build();
  }

  private final Set<RoleEntity> buildRoleSet(EnumRole formRole) {
    Role role = roleService.getByName(formRole == null ? EnumRole.USER : formRole);
    Set<RoleEntity> roleSet = new HashSet<>();
    roleSet.add(RoleMapper.domainToEntity(role));
    return roleSet;
  }

}
