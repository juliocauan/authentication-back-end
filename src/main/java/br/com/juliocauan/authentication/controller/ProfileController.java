package br.com.juliocauan.authentication.controller;

import org.openapitools.api.ProfileApi;
import org.openapitools.model.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.ProfileServiceImpl;
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

}
