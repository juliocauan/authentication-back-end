package br.com.juliocauan.authentication.controller;

import java.util.List;

import org.openapitools.api.AdminApi;
import org.openapitools.model.AlterUserRolesForm;
import org.openapitools.model.EnumRole;
import org.openapitools.model.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocauan.authentication.infrastructure.service.application.AdminServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AdminController implements AdminApi {

    private final AdminServiceImpl adminService;

    @Override
    public ResponseEntity<AlterUserRolesForm> _alterUserRole(@Valid AlterUserRolesForm alterUserRolesForm) {
      AlterUserRolesForm response = adminService.alterUserRole(alterUserRolesForm);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
  
    @Override
    public ResponseEntity<List<UserInfo>> _getAllUsers(@Valid String username, @Valid EnumRole role) {
      List<UserInfo> response = adminService.getUserInfos(username, role);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
