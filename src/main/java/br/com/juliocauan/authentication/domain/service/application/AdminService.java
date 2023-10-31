package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.UpdateUserRolesForm;

public abstract class AdminService {
    public abstract void updateUserRole(UpdateUserRolesForm alterUserRoleForm);
}
