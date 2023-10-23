package br.com.juliocauan.authentication.model.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EnumRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.juliocauan.authentication.config.TestContext;
import br.com.juliocauan.authentication.domain.model.Role;
import br.com.juliocauan.authentication.infrastructure.model.RoleEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.RoleMapper;
import br.com.juliocauan.authentication.infrastructure.repository.RoleRepositoryImpl;
import br.com.juliocauan.authentication.infrastructure.repository.UserRepositoryImpl;

class RoleMapperTest extends TestContext {

    private final Short id = 2;

    public RoleMapperTest(UserRepositoryImpl userRepository, RoleRepositoryImpl roleRepository,
            ObjectMapper objectMapper, MockMvc mockMvc) {
        super(userRepository, roleRepository, objectMapper, mockMvc);
    }

    private final Role getRole(EnumRole role){
        return new Role() {
            @Override
            public Short getId() {return id;}
            @Override
            public EnumRole getName() {return role;}
        };
    }

    private final RoleEntity getRoleEntity(EnumRole role){
        return RoleEntity.builder()
            .id(id)
            .name(role)
        .build();
    }

    @Test
    void oneDomainRoleToOneEntity(){
        RoleEntity entity = getRoleEntity(EnumRole.ADMIN);
        Role role = getRole(EnumRole.ADMIN);
        Assertions.assertEquals(entity, RoleMapper.domainToEntity(role));
    }

    @Test
    void setDomainRoleToSetEntity(){
        Set<RoleEntity> entities = new HashSet<>();
        Set<Role> roles = new HashSet<>();
        for(EnumRole name : EnumRole.values()){
            entities.add(getRoleEntity(name));
            roles.add(getRole(name));
        }
        Assertions.assertEquals(entities, RoleMapper.domainToEntity(roles));
    }

    @Test
    void authoritiesToEnumRole() {
        Set<SimpleGrantedAuthority> authorities = Stream.of(EnumRole.values())
            .map(role -> new SimpleGrantedAuthority(role.getValue())).collect(Collectors.toSet());
        Set<EnumRole> roles = Stream.of(EnumRole.values()).collect(Collectors.toSet());
        Assertions.assertEquals(roles, RoleMapper.authoritiesToEnumRole(authorities));
    }

    @Test
    void setRoleToSetEnumRole() {
        Set<EnumRole> expectedSet = Stream.of(EnumRole.values()).collect(Collectors.toSet());
        Set<Role> roles = new HashSet<>();
        for(EnumRole name : EnumRole.values()) roles.add(getRole(name));
        Assertions.assertEquals(expectedSet, RoleMapper.setRoleToSetEnumRole(roles));
    }
    
}
