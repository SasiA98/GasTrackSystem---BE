package com.teoresi.staff.entities.old;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "unit")
public class Unit extends BasicEntity implements Cloneable, Comparable<Unit>{

    private Long id;

    private String trigram;
    private String type;
    private String status;

    @Override
    public Unit clone() {
        try {
            return (Unit) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported for Unit");
        }
    }

    @Override
    public int compareTo(@NotNull Unit o) {
        return this.trigram.compareTo(o.trigram);
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id=" + id +
                ", trigram='" + trigram + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
