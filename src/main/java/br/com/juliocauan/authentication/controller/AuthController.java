package br.com.juliocauan.authentication.controller;

import org.openapitools.api.AuthApi;
import org.openapitools.model.EmailPasswordResetRequest;
import org.openapitools.model.OkResponse;
import org.openapitools.model.PasswordMatch;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.SignupFormAdmin;
import org.openapitools.model.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.PasswordResetServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AuthenticationServiceImpl;
import br.com.juliocauan.authentication.util.EmailService;
import br.com.juliocauan.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

  private final AuthenticationServiceImpl authenticationService;
  private final PasswordResetServiceImpl passwordResetTokenService;
  private final EmailService emailService;

  @Override
  public ResponseEntity<UserData> _login(SigninForm signinForm) {
    UserData userData = authenticationService.authenticate(
        signinForm.getUsername(),
        signinForm.getPassword());
    return ResponseEntity.status(HttpStatus.OK).body(userData);
  }

  @Override
  public ResponseEntity<OkResponse> _signup(SignupForm signupForm) {
    String username = signupForm.getUsername();
    String password = signupForm.getMatch().getPassword();

    PasswordUtil.validatePasswordConfirmation(signupForm.getMatch());

    authenticationService.registerUser(username, password);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OkResponse().message("User [%s] registered successfully!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _signupAdmin(SignupFormAdmin signupFormAdmin) {
    String username = signupFormAdmin.getUsername();
    String password = signupFormAdmin.getMatch().getPassword();
    String adminKey = signupFormAdmin.getAdminKey();

    PasswordUtil.validatePasswordConfirmation(signupFormAdmin.getMatch());
    PasswordUtil.validateAdminKey(adminKey);

    authenticationService.registerAdmin(username, password);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new OkResponse().message("Admin [%s] registered successfully!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _emailPasswordReset(EmailPasswordResetRequest requestBody) {
    String username = requestBody.getUsername();
    String token = passwordResetTokenService.generateToken(username);

    //TODO send this to service
    emailService.sendEmail(
        username,
        "Reset your password!",
        passwordResetTokenService.getEmailTemplate(token));

    return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        "Email sent to [%s] successfully!".formatted(username)));
  }

  @Override
  public ResponseEntity<OkResponse> _passwordReset(PasswordMatch passwordMatch, String token) {
    PasswordUtil.validatePasswordConfirmation(passwordMatch);
    passwordResetTokenService.resetPassword(passwordMatch.getPassword(), token);
    return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Password updated successfully!"));
  }

}
