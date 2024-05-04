package br.com.juliocauan.authentication.infrastructure.service.application;

import java.util.stream.Collectors;

import org.openapitools.model.UserData;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.AuthenticationService;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepository;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepository;
import br.com.juliocauan.authentication.infrastructure.security.jwt.JwtProvider;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class AuthenticationServiceImpl extends AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Override
  protected final UserRepository getUserRepository() {
    return userRepository;
  }

  @Override
  protected final RoleRepository getRoleRepository() {
    return roleRepository;
  }

  @Override
  public final UserData authenticate(String username, String password) {
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
        username, password);
    Authentication auth = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    SecurityContextHolder.getContext().setAuthentication(auth);
    
    return new UserData()
      .roles(auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
      .JWT(jwtProvider.generateToken(auth));
  }

}
