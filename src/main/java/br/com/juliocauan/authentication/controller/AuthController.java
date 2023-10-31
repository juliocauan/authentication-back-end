package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.OkMessage;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

	private final AuthenticationServiceImpl authenticationService;

	@Override
  public ResponseEntity<OkMessage> _signup(@Valid SignupForm signupForm) {
    authenticationService.validateAndRegisterNewUser(
        signupForm.getUsername(),
        signupForm.getPassword(),
        signupForm.getRole());
    return ResponseEntity.status(HttpStatus.CREATED).body(new OkMessage().body("User registered successfully!"));
  }

  @Override
  public ResponseEntity<JWTResponse> _signin(@Valid SigninForm signinForm) {
    JWTResponse response = authenticationService.authenticate(
      signinForm.getUsername(),
      signinForm.getPassword());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
