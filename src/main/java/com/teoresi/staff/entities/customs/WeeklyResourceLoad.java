package com.teoresi.staff.entities.customs;

import com.teoresi.staff.entities.Resource;
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
@Entity(name = "weekly_resource_load")
@Immutable
public class WeeklyResourceLoad extends BasicEntity {

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @Column(name = "month")
    private int month;

    @Column(name = "n_week")
    private int weekNumber;

    @Column(name = "pre_allocation")
    private boolean preAllocation;

    @Column(name = "mean_commitment_pct")
    private Integer meanCommitmentPct;

    @Column(name = "mean_hours_pct")
    private Integer hoursCommitmentPct;

    @Override
    public String toString() {
        return "WeeklyResourceLoad{" +
                "id=" + getId() + // Assuming getId() is inherited from BasicEntity
                ", unit=" + (unit != null ? unit.getId() : null) +
                ", resource=" + (resource != null ? resource.getId() : null) +
                ", month=" + month +
                ", weekNumber=" + weekNumber +
                ", preAllocation=" + preAllocation +
                ", meanCommitmentPct=" + meanCommitmentPct +
                ", hoursCommitmentPct=" + hoursCommitmentPct +
                '}';
    }
}
