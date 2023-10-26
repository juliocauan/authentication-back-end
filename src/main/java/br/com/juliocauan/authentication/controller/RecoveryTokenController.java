package br.com.juliocauan.authentication.controller;

import org.openapitools.api.PasswordResetApi;
import org.openapitools.model.NewPasswordForm;
import org.openapitools.model.OkMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.PasswordResetTokenServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class RecoveryTokenController implements PasswordResetApi {

    private final PasswordResetTokenServiceImpl resetPasswordService;
    
    @Override
    public ResponseEntity<OkMessage> _sendResetPasswordEmail(@Valid String username) {
        resetPasswordService.generateLinkAndSendEmail(username);
        return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().body(String.format(
            "Email sent to %s successfully!", username)));
    }

    @Override
    public ResponseEntity<OkMessage> _resetPasswordByLink(@Valid NewPasswordForm newPasswordForm, String token) {
        resetPasswordService.resetPassword(newPasswordForm, token);
        return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().body("Password updated successfully!"));
    }

}
