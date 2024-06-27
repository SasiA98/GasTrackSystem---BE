package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "project_costs")
public class ProjectCosts extends BasicEntity{

    @OneToOne
    @JoinColumn
    private Project project;

    @Column(name = "act_hr_cost")
    private float actHrCost;

    @Column(name = "current_hr_cost")
    private float currentEstimatedHrCost;

    @Column(name = "pre_sale_hr_cost")
    private float presaleEstimatedHrCost;

    @Override
    public String toString() {
        return "ProjectCosts{" +
                "projectId=" + (project != null ? project.getId() : null) +
                ", actHrCost=" + actHrCost +
                ", currentEstimatedHrCost=" + currentEstimatedHrCost +
                ", presaleEstimatedHrCost=" + presaleEstimatedHrCost +
                '}';
    }
}
