package com.teoresi.staff.controllers;

import com.teoresi.staff.dtos.PasswordTokenDTO;
import com.teoresi.staff.dtos.ResponseDTO;
import com.teoresi.staff.services.PasswordTokenService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("password")
public class PasswordTokenController {

    private final PasswordTokenService passwordTokenService;

    @PostMapping("/reset")
    public ResponseDTO resetPassword(@RequestBody String email) {
        return new ResponseDTO(passwordTokenService.resetPassword(email));
    }

    @PutMapping("/save")
    public ResponseDTO savePassword(@RequestBody PasswordTokenDTO passwordTokenDTO) {
        String token = passwordTokenDTO.getToken();
        String password = passwordTokenDTO.getPassword();
        return new ResponseDTO(passwordTokenService.savePassword(token, password));
    }
}
