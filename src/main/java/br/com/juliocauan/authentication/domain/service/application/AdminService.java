package br.com.juliocauan.authentication.domain.service.application;

import java.util.List;

import org.openapitools.model.AlterUserRolesForm;
import org.openapitools.model.EnumRole;
import org.openapitools.model.UserInfo;

public abstract class AdminService {
    public abstract void alterUserRole(AlterUserRolesForm alterUserRoleForm);
    public abstract List<UserInfo> getUserInfos(String username, EnumRole role);
}
