package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "resource_hourly_cost")
public class ResourceHourlyCost extends BasicEntity implements Comparable<ResourceHourlyCost> {

    @ManyToOne
    @JoinColumn
    private Resource resource;

    private LocalDate startDate;
    private float cost;

    @Override
    public int compareTo(ResourceHourlyCost other) {
        return this.startDate.compareTo(other.startDate);
    }

    @Override
    public String toString() {
        return "ResourceHourlyCost{" +
                "id=" + getId() +
                ", resource=" + (resource != null ? resource.getId() : null) +
                ", startDate=" + startDate +
                ", cost=" + cost +
                '}';
    }
}
