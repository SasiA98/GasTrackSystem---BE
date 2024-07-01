package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "licence")
public class Licence extends BasicEntity implements Cloneable{

    private Long id;
    private String name;
    private String licenceId;

    @Override
    public Licence clone() {
        try {
            return (Licence) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported for Unit");
        }
    }

}
