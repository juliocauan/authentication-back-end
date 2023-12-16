package br.com.juliocauan.authentication.infrastructure.service.application;

import org.openapitools.model.PasswordUpdateForm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.application.ProfileService;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.service.UserServiceImpl;
import br.com.juliocauan.authentication.util.PasswordUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public final class ProfileServiceImpl extends ProfileService {

    private final UserServiceImpl userService;

    @Override
    public final void updatePassword(PasswordUpdateForm passwordUpdateForm) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = new UserEntity(userService.getByUsername(username));

        PasswordUtil.validatePasswordMatch(passwordUpdateForm.getNewPasswordMatch());
        PasswordUtil.validatePasswordMatch(passwordUpdateForm.getCurrentPassword(), entity.getPassword());
        PasswordUtil.validateSecurity(passwordUpdateForm.getNewPasswordMatch().getPassword());
        
        userService.updatePassword(username, passwordUpdateForm.getNewPasswordMatch().getPassword());
    }

    @Override
    public final void closeAccount(String password) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity entity = new UserEntity(userService.getByUsername(username));
        PasswordUtil.validatePasswordMatch(password, entity.getPassword());
        userService.delete(username);
    }

}
