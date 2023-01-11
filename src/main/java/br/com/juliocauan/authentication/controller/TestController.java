package br.com.juliocauan.authentication.controller;

import org.openapitools.api.TestApi;
import org.openapitools.model.EnumRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
//TODO test this
public class TestController implements TestApi {
    
    @Override
    public ResponseEntity<String> _allAccess() {
        return ResponseEntity.status(HttpStatus.OK).body("Public Content");
    }

    @Override
    public ResponseEntity<String> _roleAccess(EnumRole role) {
        return ResponseEntity.status(HttpStatus.OK).body(role.getValue() + "Board");
    }
    
}
