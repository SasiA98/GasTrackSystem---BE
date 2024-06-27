package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import com.teoresi.staff.shared.models.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "allocation")
public class Allocation extends BasicEntity {

    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Date startDate;
    private Date endDate;

    @JoinColumn(name = "is_real_commitment")
    private boolean isRealCommitment;

    @OneToMany(mappedBy = "allocation", fetch = FetchType.EAGER)
    Set<TimesheetProject> timesheetProjects;

    private Integer hours;
    private Integer commitmentPercentage;
    private Float dailyWorkHoursQuota;


    public String getResourceName(){
        return this.resource != null ? this.resource.getName() + " " +  this.resource.getSurname() : null;
    }

    public String getProjectName(){
        return this.project != null ? this.project.getName() : null;
    }

    public Long getProjectId(){
        return this.project != null ? this.project.getId() : null;
    }
    public Long getResourceId(){
        return this.resource != null ? this.resource.getId() : null;
    }

    @Override
    public String toString() {
        return "Allocation{" +
                "id=" + id +
                ", resourceId=" + (resource != null ? resource.getId() : null) +
                ", projectId=" + (project != null ? project.getId() : null) +
                ", role=" + role +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isRealCommitment=" + isRealCommitment +
                ", hours=" + hours +
                ", commitmentPercentage=" + commitmentPercentage +
                ", dailyWorkHoursQuota=" + dailyWorkHoursQuota +
                '}';
    }
}