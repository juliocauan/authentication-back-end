package br.com.juliocauan.authentication.controller;

import org.openapitools.api.PasswordResetTokenApi;
import org.openapitools.model.EmailPasswordResetTokenRequest;
import org.openapitools.model.OkResponse;
import org.openapitools.model.PasswordMatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.PasswordResetTokenServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PasswordResetTokenController implements PasswordResetTokenApi {

    private final PasswordResetTokenServiceImpl passwordResetTokenService;
    
    @Override
    public ResponseEntity<OkResponse> _emailPasswordResetToken(EmailPasswordResetTokenRequest requestBody) {
        String username = requestBody.getUsername();
        String token = passwordResetTokenService.generateToken(username);
        passwordResetTokenService.sendEmail(username, token);
        return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
                "Email sent to %s successfully!".formatted(username)));
    }

    @Override
    public ResponseEntity<OkResponse> _resetUserPassword(PasswordMatch passwordMatch, String token) {
        passwordResetTokenService.resetPassword(passwordMatch, token);
        return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Password updated successfully!"));
    }

}
