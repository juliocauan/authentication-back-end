package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.security.service.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public final class AuthController implements AuthApi {

	private final TokenService tokenService;

  @Override
  public ResponseEntity<JWTResponse> _signinUser(@Valid SigninForm signinForm) {
    JWTResponse response = tokenService.authenticate(signinForm);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

	@Override
  public ResponseEntity<String> _signupUser(@Valid SignupForm signupForm) {
    tokenService.validateAndRegisterNewUser(signupForm);
    return ResponseEntity.status(HttpStatus.OK).body("User registered successfully!");
  }

}
