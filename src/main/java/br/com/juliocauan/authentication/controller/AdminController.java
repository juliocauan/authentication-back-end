package br.com.juliocauan.authentication.controller;

import java.util.List;

import org.openapitools.api.AdminApi;
import org.openapitools.model.EnumRole;
import org.openapitools.model.OkMessage;
import org.openapitools.model.UpdateUserRolesForm;
import org.openapitools.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AdminController implements AdminApi {

    private final AdminServiceImpl adminService;
    private final UserServiceImpl userService;

    @Override @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OkMessage> _updateUserRoles(UpdateUserRolesForm updateUserRolesForm) {
      adminService.updateUserRole(updateUserRolesForm);
      return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().body("Patched user roles successfully!"));
    }
  
    @Override @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserInfo>> _getAllUsers(String username, EnumRole role) {
      List<UserInfo> response = userService.getUserInfosByUsernameSubstringAndRole(username, role);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
