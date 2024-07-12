package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.*;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.SimpleTimeZone;

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
