package com.teoresi.staff.security.configs;

import com.teoresi.staff.security.models.JwtAuthentication;
import com.teoresi.staff.security.services.SessionService;
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
