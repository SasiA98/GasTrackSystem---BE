package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import javax.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "time_sheet_project")
public class TimesheetProject extends BasicEntity {


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "time_sheet_id")
    private Timesheet timesheet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer hours;
    private Integer preImportHours;
    private Boolean verifiedHours;
    private float cost;
    private float dailyCostQuota;
    private String note;


    public int getWorkDaysWithoutHolidays(){
        return this.timesheet.computeWorkDaysWithoutHolidays(this.startDate, this.endDate);
    }

    public boolean isInProgress(){
        return this.project.getStatus().equals("In Progress") ||
                (this.project.getEndDate() != null && this.project.getEndDate().after(Date.from(this.startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    public String getProposedWorkHours() {

        if(this.allocation != null && this.allocation.timesheetProjects != null && this.timesheet != null) {

            Set<TimesheetProject> previousTimesheetProjects = this.allocation.timesheetProjects.stream()
                        .filter(tp -> tp.getTimesheet().getStartDate().isBefore(this.timesheet.getStartDate()))
                        .collect(Collectors.toSet());

            int previousSumWorkHours = previousTimesheetProjects.stream()
                    .filter(t -> t.getHours() != null)
                    .mapToInt(TimesheetProject::getHours)
                    .sum();

            int proposedWorkHours =  computeProposedWorkHours(previousSumWorkHours);

            return proposedWorkHours + " (of " + (this.allocation.getHours() - previousSumWorkHours) + ")";
        } else
            return "";
    }

    public Integer getAllocationHours(){
        return this.allocation != null ? this.allocation.getHours() : null;
    }

    private int computeProposedWorkHours(Integer previousSumWorkHours) {

        LocalDate allocationStartDate = allocation.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate allocationEndDate = allocation.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int allocationHours = allocation.getHours();

        LocalDate tmpStartDate = timesheet.getStartDate();
        if (allocationStartDate.isAfter(tmpStartDate)){
            tmpStartDate = allocationStartDate;
        }

        LocalDate tmpEndDate = timesheet.getEndDate();
        if (allocationEndDate.isBefore(tmpEndDate))
            return allocationHours - previousSumWorkHours;

        int availableWorkHours = timesheet.computeWorkHoursWithoutHolidays(tmpStartDate, allocationEndDate);
        int workHoursToCover = allocationHours - previousSumWorkHours;

        int availableMonthWorkHours = timesheet.computeWorkHoursWithoutHolidays(tmpStartDate, tmpEndDate);
        return Math.round((((float) (availableMonthWorkHours * workHoursToCover)) / (float) availableWorkHours));

    }

    public Float getEstimatedHrCost(){
        return this.project != null ? this.project.getCurrentEstimatedHrCost() : null;
    }

    public Float getActualHrCost(){
        return this.project != null ? this.project.getActualHrCost() : null;
    }

    public String getName(){
        return this.project != null ? this.project.getName() : null;
    }

    public Long getProjectId() {
        return this.project != null ? this.project.getId() : null;
    }

    public Long getAllocationId() {
        return this.allocation != null ? this.allocation.getId() : null;
    }

    public Long getTimesheetId(){ return this.timesheet != null ? this.timesheet.getId() : null; }

    @Override
    public String toString() {
        return "TimesheetProject{" +
                "project=" + (project != null ? project.getId() : null) +
                ", timesheet=" + (timesheet != null ? timesheet.getId() : null) +
                ", allocation=" + (allocation != null ? allocation.getId() : null) +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", hours=" + hours +
                ", preImportHours=" + preImportHours +
                ", verifiedHours=" + verifiedHours +
                ", cost=" + cost +
                ", dailyCostQuota=" + dailyCostQuota +
                ", note='" + note + '\'' +
                '}';
    }
}
