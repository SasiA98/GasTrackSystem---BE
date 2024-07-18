package com.client.staff.security.services;

import com.client.staff.security.models.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public Optional<String> authenticate(String username, String password, String roles) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                password);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userDetails);
            logger.debug(String.format("Authentication succeeded for user %s", username));
            return Optional.of(jwt);
        } catch (AuthenticationException ex) {

            logger.debug(String.format("Authentication failed with credentials (%s %s) caused by %s.",
                    username,
                    password,
                    ex.getMessage())
            );
        }
        return Optional.empty();
    }


    public Optional<String> renewAuthentication(Authentication authentication) {
        if (!(authentication instanceof JwtAuthentication)) {
            logger.debug("Called renewAuthentication using an invalid authentication type.");
            return Optional.empty();
        }
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        if (!jwtService.isTokenAboutToExpire(jwtAuthentication)) {
            logger.debug("Called renewAuthentication before expiration date.");
            return Optional.empty();
        }
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtAuthentication.getUsername());
            String jwt = jwtService.generateToken(userDetails);
            logger.debug(String.format("Authentication renewal succeeded for user %s",
                    jwtAuthentication.getUsername()));
            return Optional.of(jwt);
        } catch (UsernameNotFoundException ex) {
            logger.debug(String.format("Authentication renewal failed for user: %s with reason: %s",
                    jwtAuthentication.getUsername(),
                    ex.getMessage())
            );
            return Optional.empty();
        }
    }

}