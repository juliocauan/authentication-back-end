package br.com.juliocauan.authentication.controller;

import org.openapitools.api.PasswordResetTokenApi;
import org.openapitools.model.OkMessage;
import org.openapitools.model.PasswordMatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.PasswordResetTokenServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PasswordResetTokenController implements PasswordResetTokenApi {

    private final PasswordResetTokenServiceImpl passwordResetTokenService;
    
    @Override
    public ResponseEntity<OkMessage> _sendPasswordResetTokenEmail(@Valid String username) {
        passwordResetTokenService.buildTokenAndSendEmail(username);
        return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().body(String.format(
            "Email sent to %s successfully!", username)));
    }

    @Override
    public ResponseEntity<OkMessage> _resetUserPassword(@Valid PasswordMatch passwordMatch, String token) {
        passwordResetTokenService.resetPassword(passwordMatch, token);
        return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().body("Password updated successfully!"));
    }

}
