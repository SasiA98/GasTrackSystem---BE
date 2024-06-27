package com.teoresi.staff.entities.customs;

import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "daily_resource_load")
public class DailyResourceLoad extends BasicEntity {

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private int year;

    private int month;

    @Column(name = "n_week")
    private int weekNumber;

    @Column(name = "n_day")
    private int dayNumber;

    @Column(name = "is_holiday")
    private int isHoliday;

    @Column(name = "pre_allocation")
    private boolean preAllocation;

    @Column(name = "commitment_pct")
    private Integer commitmentPct;

    @Column(name = "hours_pct")
    private Integer hoursPct;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyResourceLoad that = (DailyResourceLoad) o;
        return year == that.year &&
                dayNumber == that.dayNumber &&
                Objects.equals(resource, that.resource);
    }

    @Override
    public String toString() {
        return "DailyResourceLoad{" +
                "id=" + getId() + // Assuming getId() is inherited from BasicEntity
                ", resource=" + (resource != null ? resource.getId() : null) +
                ", year=" + year +
                ", month=" + month +
                ", weekNumber=" + weekNumber +
                ", dayNumber=" + dayNumber +
                ", isHoliday=" + isHoliday +
                ", preAllocation=" + preAllocation +
                ", commitmentPct=" + commitmentPct +
                ", hoursPct=" + hoursPct +
                '}';
    }
}
