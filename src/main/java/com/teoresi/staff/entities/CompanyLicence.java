package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "company_licence")
public class CompanyLicence extends BasicEntity implements Cloneable{

    private Long id;
    private Date endDate;
    private Date startDate;

    @ManyToOne
    @JoinColumn(name = "licence_id")
    private Licence licence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    public Long getCompanyId(){ return this.company != null ? this.company.getId() : null; }
    public Long getLicenceId(){ return this.licence != null ? this.licence.getId() : null; }


    public String getLicenceName(){ return this.licence != null ? this.licence.getName() : null; }
    public String getCompanyName(){ return this.company != null ? this.company.getName() : null; }

    @Override
    public CompanyLicence clone() {
        try {
            return (CompanyLicence) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
