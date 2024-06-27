package com.teoresi.staff.profile.controllers;

import com.teoresi.staff.profile.dtos.PatchProfileDTO;
import com.teoresi.staff.dtos.UserDTO;
import com.teoresi.staff.entities.User;
import com.teoresi.staff.mappers.UserMapper;
import com.teoresi.staff.security.models.JwtAuthentication;
import com.teoresi.staff.profile.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping
    public UserDTO partialUpdate(JwtAuthentication jwtAuthentication, @RequestBody PatchProfileDTO patchProfileDTO) {
        User user = userMapper.convertProfileDTOtoUser(patchProfileDTO);
        return userMapper.convertModelToDTO(
                profileService.partialUpdate(jwtAuthentication, user, patchProfileDTO.getNewPassword())
        );
    }

}
