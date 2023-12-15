package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.EmailPasswordResetUrlRequest;
import org.openapitools.model.JWT;
import org.openapitools.model.OkResponse;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.SignupFormAdmin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.PasswordResetTokenServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

	private final AuthenticationServiceImpl authenticationService;
  private final PasswordResetTokenServiceImpl passwordResetTokenService;

	@Override
  public ResponseEntity<OkResponse> _signup(SignupForm signupForm) {
    authenticationService.registerUser(
        signupForm.getUsername(),
        signupForm.getPassword());
    return ResponseEntity.status(HttpStatus.CREATED).body(new OkResponse().message("User registered successfully!"));
  }

  @Override
  public ResponseEntity<JWT> _login(SigninForm signinForm) {
    JWT jwt = authenticationService.authenticate(
        signinForm.getUsername(),
        signinForm.getPassword());
    return ResponseEntity.status(HttpStatus.OK).body(jwt);
  }

  @Override
  public ResponseEntity<OkResponse> _signupAdmin(SignupFormAdmin signupFormAdmin) {
    authenticationService.registerAdmin(
        signupFormAdmin.getUsername(),
        signupFormAdmin.getPassword(),
        signupFormAdmin.getAdminPassword());
    return ResponseEntity.status(HttpStatus.CREATED).body(new OkResponse().message("Admin registered successfully!"));
  }

  @Override
  public ResponseEntity<OkResponse> _emailPasswordResetUrl(EmailPasswordResetUrlRequest requestBody) {
      String username = requestBody.getUsername();
      String token = passwordResetTokenService.generateToken(username);
      passwordResetTokenService.sendEmail(username, token);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
              "Email sent to %s successfully!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _resetPassword(PasswordMatch passwordMatch, String token) {
      passwordResetTokenService.resetPassword(passwordMatch, token);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Password updated successfully!"));
  }

}
