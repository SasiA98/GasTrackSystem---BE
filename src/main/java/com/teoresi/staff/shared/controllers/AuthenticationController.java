package com.teoresi.staff.shared.controllers;

import com.teoresi.staff.security.dtos.AuthenticationRequestDTO;
import com.teoresi.staff.security.dtos.AuthenticationResponseDTO;
import com.teoresi.staff.security.services.AuthenticationService;
import com.teoresi.staff.security.services.JwtService;
import com.teoresi.staff.security.services.LoginAttemptService;
import com.teoresi.staff.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LoginAttemptService loginAttemptService;

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping
    public AuthenticationResponseDTO authenticate(@Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        Optional<String> authenticationToken = authenticationService.authenticate(
                authenticationRequestDTO.getUsername(),
                authenticationRequestDTO.getPassword(),
                authenticationRequestDTO.getRoles());

        if (authenticationToken.isPresent()) {
            return AuthenticationResponseDTO.builder().authentication(authenticationToken.get()).build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect credentials.");
        }
    }

    @PutMapping
    public AuthenticationResponseDTO refreshToken(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            Authentication authentication
    ) {
        if (authentication != null) {
            Optional<String> authenticationToken = authenticationService.renewAuthentication(authentication);
            if (authenticationToken.isPresent()) {
                return AuthenticationResponseDTO.builder().authentication(authenticationToken.get()).build();
            }
            Optional<String> currentToken = jwtService.extractTokenFromAuthorizationHeader(authorizationHeader);
            if (currentToken.isPresent()) {
                return AuthenticationResponseDTO.builder().authentication(currentToken.get()).build();
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to refresh authentication.");
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in.");
    }

}
