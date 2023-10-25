package br.com.juliocauan.authentication.infrastructure.service;

import org.openapitools.model.PasswordUpdateForm;
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
    public void alterPassword(PasswordUpdateForm passwordUpdateForm) {
        UserEntity entity = UserMapper.domainToEntity(userService.getByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName()));

        passwordService.checkPasswordConfirmation(passwordUpdateForm);
        passwordService.checkOldPassword(entity, passwordUpdateForm);
        
        entity.setPassword(passwordService.encodePassword(passwordUpdateForm.getNewPasswordMatch().getPassword()));
        userService.save(entity);
    }
    
}
