package com.client.staff.shared.components;

import com.client.staff.services.CompanyLicenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private CompanyLicenceService companyLicenceService;

    // If the server has been shot down, these methods restore data consistency

    @Scheduled(cron = "0 0 8 * * *") // " 0 sec, 0 min, 0 hour, 1 day, * every month, * every year
    public void executeDailyTask() {
        companyLicenceService.notifyAboutExpiringLicence();
    }
}