package br.com.juliocauan.authentication.domain.service;

import org.openapitools.model.ProfileRoles;

public interface AdminService {
    public ProfileRoles alterUserRole(ProfileRoles profileRoles);
}
