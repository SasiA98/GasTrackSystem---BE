package com.teoresi.staff.security.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtServiceConfig {

    private final String secretKey;

    private final Long tokenDurationMs;
    private final Integer tokenRenewBeforeExpirationMs;

    public JwtServiceConfig(
            @Value("${application.jwt.secretKey}") String secretKey,
            @Value("${application.jwt.tokenDurationMs}") Long tokenDurationMs,
            @Value("${application.jwt.tokenRenewBeforeExpirationMs}") Integer tokenRenewBeforeExpirationMs
    ) {
        this.secretKey = secretKey;
        this.tokenDurationMs = tokenDurationMs;
        this.tokenRenewBeforeExpirationMs = tokenRenewBeforeExpirationMs;
    }

}
