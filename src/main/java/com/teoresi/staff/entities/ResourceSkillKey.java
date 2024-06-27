package com.teoresi.staff.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
public class ResourceSkillKey implements Serializable {

    @Column(name = "resource_id")
    Long resourceId;

    @Column(name = "skill_id")
    Long skillId;

    @Override
    public String toString() {
        return "ResourceSkillKey{" +
                "resourceId=" + resourceId +
                ", skillId=" + skillId +
                '}';
    }

}
