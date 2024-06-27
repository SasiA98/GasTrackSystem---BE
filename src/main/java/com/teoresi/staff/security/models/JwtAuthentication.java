package com.teoresi.staff.security.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teoresi.staff.shared.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtAuthentication implements Authentication {

    private Long id;
    @JsonProperty("sub")
    private String username;
    private Long iat; // Epoch in seconds
    private Long exp; // Epoch in seconds
    private Set<Role> authorities;
    private boolean isAuthenticated;
    private Object details;
    private Object credentials;
    public Date getExpirationDate() {
        return new Date(exp * 1000);
    }

    public Date getCreationDate() {
        return new Date(iat * 1000);
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public String getName() {
        return username;
    }

    public boolean hasRole(Role role) {
        if (authorities == null) {
            return false;
        }
        return authorities.contains(role);
    }

    public boolean hasAnyRole(Collection<Role> roles) {
        for (Role role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

}
