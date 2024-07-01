package com.teoresi.staff.entities.old;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity(name = "resource_salary_details")
public class ResourceSalaryDetails extends BasicEntity {

    @ManyToOne
    @JoinColumn
    private Resource resource;

    private String ral;
    private Date ralStartDate;
    private Integer dailyAllowance;
    private Date dailyAllowanceStartDate;
    private String ccnlLevel;
    private Date ccnlLevelStartDate;


    @Override
    public String toString() {
        return "ResourceSalaryDetails{" +
                "id=" + getId() +
                ", resourceId=" + (resource != null ? resource.getId() : null) +
                ", ral='" + ral + '\'' +
                ", ralStartDate=" + ralStartDate +
                ", dailyAllowance=" + dailyAllowance +
                ", dailyAllowanceStartDate=" + dailyAllowanceStartDate +
                ", ccnlLevel='" + ccnlLevel + '\'' +
                ", ccnlLevelStartDate=" + ccnlLevelStartDate +
                '}';
    }
}
