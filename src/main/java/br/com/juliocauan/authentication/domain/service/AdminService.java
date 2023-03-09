package br.com.juliocauan.authentication.domain.service;

import java.util.List;

import org.openapitools.model.EnumRole;
import org.openapitools.model.ProfileRoles;
import org.openapitools.model.UserInfo;

public interface AdminService {
    ProfileRoles alterUserRole(ProfileRoles profileRoles);
    List<UserInfo> getUserInfos(String username, EnumRole role);
}
