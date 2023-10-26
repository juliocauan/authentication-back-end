package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.OkMessage;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.application.JwtServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

	private final JwtServiceImpl jwtService;

	@Override
  public ResponseEntity<OkMessage> _signupUser(@Valid SignupForm signupForm) {
    jwtService.validateAndRegisterNewUser(
        signupForm.getUsername(),
        signupForm.getPassword(),
        signupForm.getRole());
    return ResponseEntity.status(HttpStatus.CREATED).body(new OkMessage().body("User registered successfully!"));
  }

  @Override
  public ResponseEntity<JWTResponse> _signinUser(@Valid SigninForm signinForm) {
    JWTResponse response = jwtService.authenticate(signinForm);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
