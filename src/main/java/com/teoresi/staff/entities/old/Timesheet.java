package com.teoresi.staff.entities.old;


import com.teoresi.staff.libs.utils.Holiday;
import com.teoresi.staff.shared.entities.BasicEntity;
import com.teoresi.staff.shared.services.HolidayManagementService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "time_sheet")
public class Timesheet extends BasicEntity {

    private Long id;

    @Transient
    private final HolidayManagementService holidayManagementService = new HolidayManagementService();
    @Transient
    private final Logger logger = LoggerFactory.getLogger(Timesheet.class);
    private static final String ERROR_RETRIEVING_HOLIDAYS = "Error during retrieving holidays from json";


    @ManyToOne
    @JoinColumn
    private Resource resource;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private int totWorkHours;

    @OneToMany(mappedBy = "timesheet", fetch = FetchType.EAGER)
    Set<TimesheetProject> relatedProjects;


    public void refreshTotWorkHours(){
        this.totWorkHours = computeWorkHours(this.startDate, this.endDate);
    }

    public Timesheet(LocalDate startDate, LocalDate endDate, Resource resource){
        this.startDate = startDate;
        this.resource = resource;
        this.endDate = endDate;
        this.totWorkHours = computeWorkHours(startDate, endDate);
    }

    public int getRemainingWorkHours(){
        int sumWorkHours = 0;

        if(relatedProjects != null){
            for(TimesheetProject timesheetProject : relatedProjects){
                if (timesheetProject.getHours() != null)
                    sumWorkHours = sumWorkHours + timesheetProject.getHours();
            }
        }

        return  totWorkHours - sumWorkHours;
    }

    private int computeWorkHours(LocalDate startDate, LocalDate endDate) {
        return computeWorkDays(startDate,endDate) * 8;
    }

    private int computeWorkDays(LocalDate startDate, LocalDate endDate){
        int workingDays = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                workingDays++;
        }
        return  workingDays;
    }

    public int computeWorkHoursWithoutHolidays(LocalDate startDate, LocalDate endDate){
        return computeWorkDaysWithoutHolidays(startDate,endDate) * 8;
    }

    public int computeWorkDaysWithoutHolidays(LocalDate startDate, LocalDate endDate){

        int workDays = computeWorkDays(startDate,endDate);
        int holidayDays = 0;

        try {
            Set<Holiday> holidays = holidayManagementService.retrieveHolidays(startDate, endDate);

            for (Holiday holiday : holidays) {
                LocalDate date = holiday.getDate();

                if(!(date.getDayOfWeek() == DayOfWeek.SATURDAY) && !(date.getDayOfWeek() == DayOfWeek.SUNDAY)
                        && !date.isAfter(endDate) && !date.isBefore(startDate))
                    holidayDays += 1;
            }

        } catch (IOException exception){
            String message = String.format(ERROR_RETRIEVING_HOLIDAYS);
            logger.debug(message);
        }

        return workDays - holidayDays;
    }


    @Override
    public String toString() {
        return "Timesheet{" +
                "id=" + id +
                ", resource=" + (resource != null ? resource.getId() : null) +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totWorkHours=" + totWorkHours +
                '}';
    }

}
