package br.com.juliocauan.authentication.controller;

import org.openapitools.api.ProfileApi;
import org.openapitools.model.OkMessage;
import org.openapitools.model.PasswordUpdate;
import org.openapitools.model.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.ProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ProfileController implements ProfileApi {
    
    private final ProfileServiceImpl profileService;

    @Override
    public ResponseEntity<Profile> _profileContent() {
        Profile profile = profileService.getProfileContent();
        return ResponseEntity.status(HttpStatus.OK).body(profile);
    }

    @Override
    public ResponseEntity<OkMessage> _alterUserPassword(@Valid PasswordUpdate passwordUpdate) {
        profileService.alterPassword(passwordUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().message("Password updated successfully!"));
    }

}
