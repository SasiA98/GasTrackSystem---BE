package com.teoresi.staff.profile.services;

import com.teoresi.staff.entities.User;
import com.teoresi.staff.security.models.JwtAuthentication;
import com.teoresi.staff.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private static final String INVALID_PASSWORD = "Unable to change password: invalid current password";

    public User getProfile(JwtAuthentication jwtAuthentication) {
        return userService.getById(jwtAuthentication.getId());
    }

    public User partialUpdate(JwtAuthentication jwtAuthentication, User userToEdit, String newPassword) {
        var user = userService.getById(jwtAuthentication.getId());
        if (userToEdit.getPassword() != null) {
            if (!isUserPasswordCorrect(user.getPassword(), userToEdit.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_PASSWORD);
            }
            userToEdit.setPassword(newPassword);
        }
        userToEdit.setId(jwtAuthentication.getId());
        return userService.partialUpdate(userToEdit);
    }

    private boolean isUserPasswordCorrect(String encodedPassword, String rawPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
