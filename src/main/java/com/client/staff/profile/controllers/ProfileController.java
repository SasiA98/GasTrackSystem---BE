package com.client.staff.profile.controllers;

import com.client.staff.dtos.UserDTO;
import com.client.staff.mappers.UserMapper;
import com.client.staff.profile.services.ProfileService;
import com.client.staff.security.models.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("profile")
public class ProfileController {

    private final UserMapper userMapper;
    private final ProfileService profileService;

    @GetMapping
    public UserDTO getProfile(JwtAuthentication jwtAuthentication) {
        return userMapper.convertModelToDTO(profileService.getProfile(jwtAuthentication));
    }

}
