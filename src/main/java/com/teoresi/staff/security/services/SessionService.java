package com.teoresi.staff.security.services;

import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.security.models.JwtAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SessionService {

    @Autowired
    UserSessionService userSessionService;
    private final Logger logger = LoggerFactory.getLogger(SessionService.class);

    public JwtAuthentication getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof JwtAuthentication) {
            return (JwtAuthentication) authentication;
        } else {
            // Se l'utente non è autenticato con un token JWT, restituisci null
            return null;
        }
    }

    public String getRole() {
        return getCurrentUser().getAuthorities().toArray()[0].toString();
    }

    public boolean hasRole(Role role) {
        var jwtAuthentication = getCurrentUser();
        return jwtAuthentication.hasRole(role);
    }

    public boolean hasAnyRole(Collection<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        var jwtAuthentication = getCurrentUser();
        return jwtAuthentication.hasAnyRole(roles);
    }
}
