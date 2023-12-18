package br.com.juliocauan.authentication.controller;

import java.util.List;
import java.util.Set;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController implements AdminApi {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
  
    @Override
    public ResponseEntity<List<UserInfo>> _getUsers(String usernameContains, String role) {
      List<UserInfo> response = userService.getUserInfos(usernameContains, role);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<OkResponse> _updateUserRoles(UpdateUserRolesForm updateUserRolesForm) {
      String username = updateUserRolesForm.getUsername();
      Set<String> newRoles = updateUserRolesForm.getRoles();
      userService.updateRoles(updateUserRolesForm.getUsername(), updateUserRolesForm.getRoles());
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse()
        .message("Patched [%s] successfully! Roles: %s".formatted(username, newRoles)));
    }

    @Override
    public ResponseEntity<OkResponse> _deleteUser(DeleteUserRequest deleteUserRequest) {
      String username = deleteUserRequest.getUsername();
      userService.delete(username);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        String.format("User [%s] deleted successfully!", username)));
    }

    @Override
    public ResponseEntity<List<String>> _getRoles(String contains) {
      List<String> response = roleService.getAll(contains);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<OkResponse> _registerRole(RegisterRoleRequest registerRoleRequest) {
      roleService.register(registerRoleRequest.getRole());
      return ResponseEntity.status(HttpStatus.CREATED).body(new OkResponse().message(
        String.format("Role [%s] registered successfully!", registerRoleRequest.getRole())));
    }

    @Override
    @Transactional
    public ResponseEntity<OkResponse> _deleteRole(DeleteRoleRequest deleteRoleRequest) {
      Role role = roleService.getByName(deleteRoleRequest.getRole());
      List<UserInfo> users = userService.getUserInfos(null, role.getName());
      users.forEach(user -> {
          Set<String> roles = user.getRoles();
          roles.remove(role.getName());
          userService.updateRoles(user.getUsername(), roles);
      });
      roleService.delete(role);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        String.format("Role [%s] deleted successfully!", role)));
    }
    

}
