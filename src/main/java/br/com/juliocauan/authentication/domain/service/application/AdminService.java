package br.com.juliocauan.authentication.domain.service.application;

import org.openapitools.model.AlterUserRolesForm;

public abstract class AdminService {
    public abstract void updateUserRole(AlterUserRolesForm alterUserRoleForm);
}
