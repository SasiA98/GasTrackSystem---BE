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
    private Date expiryDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "licence_id")
    private Licence licence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    private boolean emailSent;

    private String directory;


    public static String computeDirectory(Company company, Licence licence) {
        return company.getDirectory() + licence.getDirectory();
    }

    @Override
    public CompanyLicence clone() {
        try {
            return (CompanyLicence) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }


    public String getIsEmailSent(){ return this.emailSent ? "SI" : "NO"; }
}
