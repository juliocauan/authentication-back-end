package br.com.juliocauan.authentication.controller;

import java.util.List;

import org.openapitools.api.AdminApi;
import org.openapitools.model.EnumRole;
import org.openapitools.model.ProfileRoles;
import org.openapitools.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.AdminServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AdminController implements AdminApi {

    private final AdminServiceImpl adminService;

    @Override
    public ResponseEntity<ProfileRoles> _alterUserRole(@Valid ProfileRoles profileRoles) {
      ProfileRoles response = adminService.alterUserRole(profileRoles);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
  
    @Override
    public ResponseEntity<List<UserInfo>> _getAllUsers(@Valid String username, @Valid EnumRole role) {
      List<UserInfo> response = adminService.getUserInfos(username, role);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
