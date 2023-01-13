package br.com.juliocauan.authentication.controller;

import org.openapitools.api.TestApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements TestApi {
    
    @Override
    public ResponseEntity<String> _allAccess() {
        return ResponseEntity.status(HttpStatus.OK).body("Public Content");
    }

    @Override
    public ResponseEntity<String> _adminAccess() {
        return ResponseEntity.status(HttpStatus.OK).body("Admin Board");
    }

    @Override
    public ResponseEntity<String> _managerAccess() {
        return ResponseEntity.status(HttpStatus.OK).body("Manager Board");
    }

    @Override
    public ResponseEntity<String> _userAccess() {
        return ResponseEntity.status(HttpStatus.OK).body("User Board");
    }
    
}
