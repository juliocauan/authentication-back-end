package br.com.juliocauan.authentication.controller;

import org.openapitools.api.ResetPasswordApi;
import org.openapitools.model.OkMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.RecoveryTokenServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class RecoveryTokenController implements ResetPasswordApi {

    private final RecoveryTokenServiceImpl resetPasswordService;
    
    @Override
    public ResponseEntity<OkMessage> _sendPasswordResetEmail(@Valid String username) {
        resetPasswordService.generateLinkAndSendEmail(username);
        return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().message(String.format(
            "Email sent to %s successfully!", username)));
    }
    
}
