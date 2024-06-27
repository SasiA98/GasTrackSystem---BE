package com.teoresi.staff.shared.configs;

import com.teoresi.staff.security.configs.ScsAuditorAwareImpl;
import com.teoresi.staff.security.services.SessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider(SessionService sessionService) {
        return new ScsAuditorAwareImpl(sessionService);
    }

}
