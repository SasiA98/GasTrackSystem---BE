package com.client.staff.profile.services;

import com.client.staff.entities.User;
import com.client.staff.security.models.JwtAuthentication;
import com.client.staff.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private static final String INVALID_PASSWORD = "Unable to change password: invalid current password";

    public User getProfile(JwtAuthentication jwtAuthentication) {
        return userService.getById(jwtAuthentication.getId());
    }
}
