package com.teoresi.staff.shared.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentService {

    @Autowired
    private org.springframework.core.env.Environment environment;

    public boolean isDevelopmentMode() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.equalsIgnoreCase("dev") || profile.equalsIgnoreCase("development")) {
                return true;
            }
        }
        return false;
    }
}

