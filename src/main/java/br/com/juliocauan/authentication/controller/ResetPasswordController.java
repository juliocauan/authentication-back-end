package br.com.juliocauan.authentication.controller;

import org.openapitools.api.ResetPasswordApi;
import org.openapitools.model.OkMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ResetPasswordController implements ResetPasswordApi {
    
    @Override
    public ResponseEntity<OkMessage> _sendPasswordResetEmail(@Valid String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method '_sendPasswordResetEmail'");
    }
    
}
