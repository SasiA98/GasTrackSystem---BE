package com.client.staff.security.configs;

import com.client.staff.security.models.JwtAuthentication;
import com.client.staff.security.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@RequiredArgsConstructor
public class ScsAuditorAwareImpl implements AuditorAware<Long> {

    private final SessionService sessionService;

    @Override
    public Optional<Long> getCurrentAuditor() {
        JwtAuthentication jwtAuthentication = sessionService.getCurrentUser();
        return jwtAuthentication != null
                ? Optional.of(jwtAuthentication.getId())
                : Optional.empty();
    }
}
