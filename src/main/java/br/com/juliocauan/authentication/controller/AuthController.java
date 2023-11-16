package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.BearerToken;
import org.openapitools.model.OkMessage;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

	private final AuthenticationServiceImpl authenticationService;

	@Override
  public ResponseEntity<OkMessage> _signup(SignupForm signupForm) {
    authenticationService.registerUser(
        signupForm.getUsername(),
        signupForm.getPassword(),
        signupForm.getRole());
    return ResponseEntity.status(HttpStatus.CREATED).body(new OkMessage().body("User registered successfully!"));
  }

  @Override
  public ResponseEntity<BearerToken> _signin(SigninForm signinForm) {
    return ResponseEntity.status(HttpStatus.OK).body(authenticationService
      .authenticate(
        signinForm.getUsername(),
        signinForm.getPassword()));
  }

}
