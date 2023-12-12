package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.JWT;
import org.openapitools.model.OkResponse;
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
  public ResponseEntity<OkResponse> _signup(SignupForm signupForm) {
    authenticationService.registerUser(
        signupForm.getUsername(),
        signupForm.getPassword());
    return ResponseEntity.status(HttpStatus.CREATED).body(new OkResponse().message("User registered successfully!"));
  }

  @Override
  public ResponseEntity<JWT> _signin(SigninForm signinForm) {
    JWT jwt = authenticationService.authenticate(
        signinForm.getUsername(),
        signinForm.getPassword());
    return ResponseEntity.status(HttpStatus.OK).body(jwt);
  }

}
