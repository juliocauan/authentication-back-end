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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class JwtService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserServiceImpl userService;
  private final RoleServiceImpl roleService;
  private final PasswordEncoder encoder;

  public JWTResponse authenticate(SigninForm signinForm) {
    Authentication auth = authenticationManager.authenticate(parseAsAuthToken(signinForm));
    SecurityContextHolder.getContext().setAuthentication(auth);
    String token = jwtProvider.generateToken(auth);
    UserDetails userPrincipal = (UserDetails) auth.getPrincipal();
    List<EnumRole> roles = userPrincipal.getAuthorities().stream()
        .map(item -> EnumRole.fromValue(item.getAuthority()))
        .collect(Collectors.toList());
    return parseAsJWTResponse(token, userPrincipal, roles);
  }

  public void validateAndRegisterNewUser(SignupForm signupForm) {
    userService.checkDuplicatedUsername(signupForm.getUsername());
    userService.checkDuplicatedEmail(signupForm.getEmail());
    UserEntity userEntity = UserMapper.formToEntityWithEncodedPassword(signupForm, encoder);
    Set<RoleEntity> roles = new HashSet<>();
    signupForm.getRoles().forEach(role -> roles.add(RoleMapper.domainToEntity(roleService.getByName(role))));
    userEntity.setRoles(roles);
    userService.save(userEntity);
  }

  private UsernamePasswordAuthenticationToken parseAsAuthToken(SigninForm signinForm) {
    return new UsernamePasswordAuthenticationToken(signinForm.getUsername(), signinForm.getPassword());
  }

  private JWTResponse parseAsJWTResponse(String token, UserDetails userPrincipal, List<EnumRole> roles) {
    return new JWTResponse()
        .token(token)
        .type(EnumToken.BEARER)
        .username(userPrincipal.getUsername())
        .roles(roles);
  }

}
