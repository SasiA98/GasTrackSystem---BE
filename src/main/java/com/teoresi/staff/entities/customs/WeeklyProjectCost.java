package com.teoresi.staff.entities.customs;

import com.teoresi.staff.entities.Project;
import com.teoresi.staff.entities.Unit;
import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "weekly_project_cost")
@Immutable
public class WeeklyProjectCost extends BasicEntity {

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Column(name = "n_week")
    private int weekNumber;

    @Column(name = "cumulative_est_cost_pct")
    private Integer cumulativeEstimatedCostPct;

    @Column(name = "cumulative_est_cost")
    private int cumulativeEstimatedCost;

    @Column(name = "cumulative_act_cost_pct")
    private Integer cumulativeActualCostPct;

    @Column(name = "cumulative_act_cost")
    private int cumulativeActualCost;


    @Override
    public String toString() {
        return "DailyProjectCost{" +
                "id=" + getId() + // Assuming getId() is inherited from BasicEntity
                ", project=" + (project != null ? project.getId() : null) +
                ", unit=" + (unit != null ? unit.getId() : null) +
                ", year=" + year +
                ", month=" + month +
                ", weekNumber=" + weekNumber +
                ", cumulativeEstimatedCostPct=" + cumulativeEstimatedCostPct +
                ", cumulativeEstimatedCost=" + cumulativeEstimatedCost +
                ", cumulativeActualCostPct=" + cumulativeActualCostPct +
                ", cumulativeActualCost=" + cumulativeActualCost +
                '}';
    }

}
