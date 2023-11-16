package br.com.juliocauan.authentication.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.api.AdminApi;
import org.openapitools.model.EnumRole;
import org.openapitools.model.OkMessage;
import org.openapitools.model.UpdateUserRolesForm;
import org.openapitools.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.infrastructure.service.RoleServiceImpl;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AdminController implements AdminApi {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;

    @Override @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OkMessage> _updateUserRoles(UpdateUserRolesForm updateUserRolesForm) {
      User user = userService.getByUsername(updateUserRolesForm.getUsername());
      Set<Role> roles = updateUserRolesForm.getRoles().stream()
        .map(roleService::getByName)
        .collect(Collectors.toSet());
      userService.updateRoles(user, roles);
      return ResponseEntity.status(HttpStatus.OK).body(new OkMessage().body("Patched user roles successfully!"));
    }
  
    @Override @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserInfo>> _getAllUsers(String username, EnumRole role) {
      List<UserInfo> response = userService.getUserInfosByUsernameSubstringAndRole(username, role);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
