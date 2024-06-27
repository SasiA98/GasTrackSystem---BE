package com.teoresi.staff.entities.customs;

import com.teoresi.staff.entities.Project;
import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "daily_project_cost")
public class DailyProjectCost extends BasicEntity {

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private int year;

    private int month;

    @Column(name = "n_week")
    private int weekNumber;

    @Column(name = "n_day")
    private int dayNumber;

    @Column(name = "est_cost_pct")
    private Float estimatedCostPct;

    @Column(name = "est_cost")
    private float estimatedCost;

    @Column(name = "act_cost_pct")
    private Float actualCostPct;

    @Column(name = "act_cost")
    private float actualCost;

    @Override
    public String toString() {
        return "DailyProjectCost{" +
                "id=" + getId() + // Assuming getId() is inherited from BasicEntity
                ", project=" + (project != null ? project.getId() : null) +
                ", year=" + year +
                ", month=" + month +
                ", weekNumber=" + weekNumber +
                ", dayNumber=" + dayNumber +
                ", estimatedCostPct=" + estimatedCostPct +
                ", estimatedCost=" + estimatedCost +
                ", actualCostPct=" + actualCostPct +
                ", actualCost=" + actualCost +
                '}';
    }
}
