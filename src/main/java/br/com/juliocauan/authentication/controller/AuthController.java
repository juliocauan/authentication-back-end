package br.com.juliocauan.authentication.controller;

import java.util.List;

import org.openapitools.api.AuthApi;
import org.openapitools.model.EnumRole;
import org.openapitools.model.JWTResponse;
import org.openapitools.model.Profile;
import org.openapitools.model.ProfileRoles;
import org.openapitools.model.SigninForm;
import org.openapitools.model.SignupForm;
import org.openapitools.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.security.service.JwtService;
import br.com.juliocauan.authentication.infrastructure.service.AdminServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AuthController implements AuthApi {

	private final JwtService jwtService;
  private final AdminServiceImpl adminService;

	@Override
  public ResponseEntity<String> _signupUser(@Valid SignupForm signupForm) {
    jwtService.validateAndRegisterNewUser(signupForm);
    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
  }

  @Override
  public ResponseEntity<JWTResponse> _signinUser(@Valid SigninForm signinForm) {
    JWTResponse response = jwtService.authenticate(signinForm);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  public ResponseEntity<Profile> _profileContent() {
    Profile profile = jwtService.getProfileContent();
    return ResponseEntity.status(HttpStatus.OK).body(profile);
  }

  @Override
  public ResponseEntity<ProfileRoles> _alterUserRole(@Valid ProfileRoles profileRoles) {
    ProfileRoles response = adminService.alterUserRole(profileRoles);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  public ResponseEntity<List<UserInfo>> _getAllUsers(@Valid String username, @Valid EnumRole role) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method '_getAllUsers'");
  }

}
