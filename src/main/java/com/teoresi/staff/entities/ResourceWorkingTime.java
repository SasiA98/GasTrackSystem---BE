package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "resource_working_time")
public class ResourceWorkingTime extends BasicEntity implements Comparable<ResourceWorkingTime> {

    @ManyToOne
    @JoinColumn
    private Resource resource;

    private LocalDate startDate;
    private int workingTime;

    @Override
    public int compareTo(ResourceWorkingTime other) {
        return this.startDate.compareTo(other.startDate);
    }

    public float getDailyWorkingTime(){
        return (float) workingTime / 5;
    }

    @Override
    public String toString() {
        return "ResourceWorkingTime{" +
                "resource=" + (resource != null ? resource.getId() : null) +
                ", startDate=" + startDate +
                ", workingTime=" + workingTime +
                '}';
    }
}
