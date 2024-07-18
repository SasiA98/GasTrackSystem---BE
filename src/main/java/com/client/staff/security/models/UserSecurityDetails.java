package com.client.staff.security.models;

import com.client.staff.shared.models.Role;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserSecurityDetails implements UserDetails {

    private Long id;
    @EqualsAndHashCode.Include
    private String username;
    private String password;
    private String token;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private Set<Role> authorities;
}