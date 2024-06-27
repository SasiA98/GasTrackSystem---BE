package com.teoresi.staff.shared.components;

import com.teoresi.staff.services.customs.ProjectCostService;
import com.teoresi.staff.services.customs.ResourceLoadService;
import com.teoresi.staff.services.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final TimesheetService timesheetService;
    private final ResourceLoadService resourceLoadService;
    private final ProjectCostService projectCostService;
    private final EnvironmentService environmentService;


    @PostConstruct
    public void postConstruct() {

        if(!environmentService.isDevelopmentMode())
            return;

        timesheetService.initializeTimesheetsForExistingResources();
        resourceLoadService.refreshTheLoadForAllResources(LocalDate.now().getYear());
        projectCostService.refreshGanttForAllProjects(LocalDate.now().getYear());

    }



    // If the server has been shot down, these methods restore data consistency

    @Scheduled(cron = "0 0 0 1 * *") // " 0 sec, 0 min, 0 hour, 1 day, * every month, * every year
    public void executeMonthlyTask() {
        timesheetService.createTimesheetsForAllResourcesMonthly();
    }

    @Scheduled(cron = "0 0 0 1 1 *") // " 0 sec, 0 min, 0 hour, 1 day, 1 every month, * every year
    public void executeYearlyTask() {
        resourceLoadService.refreshTheLoadForAllResources(LocalDate.now().getYear());
    }

}