package br.com.juliocauan.authentication.infrastructure.service;

import org.openapitools.model.PasswordUpdate;
import org.openapitools.model.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.ProfileService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserServiceImpl userService;
    private final PasswordServiceImpl passwordService;
    
    @Override
    public Profile getProfileContent() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Profile().username(user.getUsername());
    }

    @Override
    public void alterPassword(PasswordUpdate passwordUpdate) {
        UserEntity entity = UserMapper.domainToEntity(userService.getByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName()));

        passwordService.checkPasswordConfirmation(passwordUpdate);
        passwordService.checkOldPassword(entity, passwordUpdate);
        
        entity.setPassword(passwordService.encodePassword(passwordUpdate.getNewPassword()));
        userService.save(entity);
    }
    
}
