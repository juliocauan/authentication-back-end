package br.com.juliocauan.authentication.infrastructure.service;

import org.openapitools.model.PasswordUpdate;
import org.openapitools.model.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.juliocauan.authentication.domain.service.ProfileService;
import br.com.juliocauan.authentication.infrastructure.exception.InvalidOldPasswordException;
import br.com.juliocauan.authentication.infrastructure.exception.PasswordConfirmationException;
import br.com.juliocauan.authentication.infrastructure.model.UserEntity;
import br.com.juliocauan.authentication.infrastructure.model.mapper.UserMapper;
import br.com.juliocauan.authentication.infrastructure.security.model.UserPrincipal;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserServiceImpl userService;
    private final PasswordEncoder encoder;
    
    @Override
    public Profile getProfileContent() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new Profile().username(user.getUsername());
    }

    @Override
    public void alterPassword(PasswordUpdate passwordUpdate) {
        UserEntity entity = UserMapper.domainToEntity(userService.getByUsername(
            SecurityContextHolder.getContext().getAuthentication().getName()));

        checkPasswordConfirmation(passwordUpdate);
        checkOldPassword(entity, passwordUpdate);
        
        entity.setPassword(encoder.encode(passwordUpdate.getNewPassword()));
        userService.save(entity);
    }

    private void checkPasswordConfirmation(PasswordUpdate passwordUpdate){
        String newPassword = passwordUpdate.getNewPassword();
        String confirmationPassword = passwordUpdate.getNewPasswordConfirmation();
        if(!newPassword.equals(confirmationPassword))
            throw new PasswordConfirmationException("Confirmation and new password are different!");
    }

    private void checkOldPassword(UserEntity entity, PasswordUpdate passwordUpdate){
        String oldPassword = passwordUpdate.getOldPassword();
        if(!encoder.matches(oldPassword, entity.getPassword()))
            throw new InvalidOldPasswordException("Wrong old password!");
    }
    
}
