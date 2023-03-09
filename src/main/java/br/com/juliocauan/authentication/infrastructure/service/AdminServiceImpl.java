package br.com.juliocauan.authentication.infrastructure.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openapitools.model.EnumRole;
import org.openapitools.model.ProfileRoles;
import org.openapitools.model.UserInfo;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.domain.model.User;
import br.com.juliocauan.authentication.domain.service.AdminService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    
    @Override
    public ProfileRoles alterUserRole(ProfileRoles profileRoles) {
        UserEntity userEntity = UserMapper.domainToEntity(userService.getByUsername(profileRoles.getUsername()));
        Set<Role> roles = profileRoles.getRoles().stream().map(roleService::getByName).collect(Collectors.toSet());
        userEntity.setRoles(RoleMapper.domainToEntity(roles));
        userEntity = UserMapper.domainToEntity(userService.save(userEntity));
        profileRoles.username(userEntity.getUsername()).roles(userEntity.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return profileRoles;
    }

    @Override
    public List<UserInfo> getUserInfos(String username, EnumRole role) {
        List<User> users = userService.getAllUsers(username, role);
        List<UserInfo> userInfos = users.stream().map(UserMapper::domainToUserInfo).collect(Collectors.toList());
        return userInfos;
    }
    
}
