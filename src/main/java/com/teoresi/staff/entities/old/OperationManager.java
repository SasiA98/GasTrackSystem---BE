package com.teoresi.staff.entities.old;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name="operation_manager")
public class OperationManager extends BasicEntity {

    private Long id;
    private String legalEntity;
    private String industry;
    private String name;
    private String trigram;
    private String roles;
    private String reportsTo;

    @Override
    public String toString() {
        return "OperationManager{" +
                "id=" + id +
                ", legalEntity='" + legalEntity + '\'' +
                ", industry='" + industry + '\'' +
                ", name='" + name + '\'' +
                ", trigram='" + trigram + '\'' +
                ", roles='" + roles + '\'' +
                ", reportsTo='" + reportsTo + '\'' +
                '}';
    }
}
