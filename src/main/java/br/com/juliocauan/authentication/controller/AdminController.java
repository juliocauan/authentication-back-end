package br.com.juliocauan.authentication.controller;

import java.util.List;

import org.openapitools.api.AdminApi;
import org.openapitools.model.DeleteRoleRequest;
import org.openapitools.model.DeleteUserRequest;
import org.openapitools.model.OkResponse;
import org.openapitools.model.RegisterRoleRequest;
import org.openapitools.model.UpdateUserRolesForm;
import org.openapitools.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController implements AdminApi {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    private final AdminServiceImpl adminService;

    @Override
    public ResponseEntity<OkResponse> _updateUserRoles(UpdateUserRolesForm updateUserRolesForm) {
      userService.updateRoles(updateUserRolesForm.getUsername(), updateUserRolesForm.getRoles());
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message("Patched user roles successfully!"));
    }
  
    @Override
    public ResponseEntity<List<UserInfo>> _getUsers(String username, String role) {
      List<UserInfo> response = userService.getUserInfosByUsernameSubstringAndRole(username, role);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<OkResponse> _registerRole(RegisterRoleRequest registerRoleRequest) {
      roleService.save(registerRoleRequest.getRole());
      return ResponseEntity.status(HttpStatus.CREATED).body(new OkResponse().message(
        String.format("Role %s registered successfully!", registerRoleRequest.getRole())));
    }

    @Override
    public ResponseEntity<OkResponse> _deleteUser(DeleteUserRequest deleteUserRequest) {
      String username = deleteUserRequest.getUsername();
      userService.delete(username);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        String.format("User %s deleted successfully!", username)));
    }

    @Override
    public ResponseEntity<List<String>> _getRoles(String contains) {
      List<String> response = roleService.getAllRoles(contains);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<OkResponse> _deleteRole(DeleteRoleRequest deleteRoleRequest) {
      String role = deleteRoleRequest.getRole();
      adminService.delete(role);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        String.format("Role %s deleted successfully!", role)));
    }
    

}
