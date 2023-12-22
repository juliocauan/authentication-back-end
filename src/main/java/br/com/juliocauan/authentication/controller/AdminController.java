package br.com.juliocauan.authentication.controller;

import java.util.List;
import java.util.Set;

import org.openapitools.api.AdminApi;
import org.openapitools.model.DeleteRoleRequest;
import org.openapitools.model.DeleteUserRequest;
import org.openapitools.model.OkResponse;
import org.openapitools.model.Page;
import org.openapitools.model.RegisterRoleRequest;
import org.openapitools.model.UpdateUserRolesForm;
import org.openapitools.model.UserInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController implements AdminApi {

    private final AdminServiceImpl adminService;
  
    @Override
    public ResponseEntity<List<UserInfo>> _getUsers(String usernameContains, String role, Page page) {
      Pageable pageable = PageRequest.of(page.getNumber(), page.getSize());
      
      List<UserInfo> response = adminService.getUserInfos(usernameContains, role, pageable);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<OkResponse> _updateUserRoles(UpdateUserRolesForm updateUserRolesForm) {
      String username = updateUserRolesForm.getUsername();
      Set<String> newRoles = updateUserRolesForm.getRoles();

      adminService.updateUserRoles(username, newRoles);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse()
        .message("Patched [%s] successfully! Roles: %s".formatted(username, newRoles)));
    }

    @Override
    public ResponseEntity<OkResponse> _deleteUser(DeleteUserRequest deleteUserRequest) {
      String username = deleteUserRequest.getUsername();

      adminService.deleteUser(username);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        String.format("User [%s] deleted successfully!", username)));
    }

    @Override
    public ResponseEntity<List<String>> _getRoles(String nameContains) {
      List<String> response = adminService.getAllRoles(nameContains);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<OkResponse> _registerRole(RegisterRoleRequest registerRoleRequest) {
      String role = registerRoleRequest.getRole();
      
      adminService.registerRole(role);
      return ResponseEntity.status(HttpStatus.CREATED).body(new OkResponse().message(
        String.format("Role [%s] registered successfully!", role)));
    }

    @Override
    public ResponseEntity<OkResponse> _deleteRole(DeleteRoleRequest deleteRoleRequest) {
      String roleName = deleteRoleRequest.getRole();

      adminService.deleteRole(roleName);
      return ResponseEntity.status(HttpStatus.OK).body(new OkResponse().message(
        String.format("Role [%s] deleted successfully!", roleName)));
    }
    

}
