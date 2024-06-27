package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import com.teoresi.staff.shared.models.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "resource")
public class Resource extends BasicEntity implements Cloneable, Comparable<Resource>{

    private Long id;

    private Integer employeeId;
    private String name;

    @Column(unique = true)
    private String email;

    private String surname;
    private Date birthDate;
    private Date hiringDate;
    private Date leaveDate;

    private int lastWorkingTime;
    private Date lastWorkingTimeStartDate;

    @ManyToOne
    @JoinColumn
    private Unit unit;

    private Float lastHourlyCost;
    private Date lastHourlyCostStartDate;


    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "resources_roles", joinColumns = @JoinColumn(name = "resource_id"))
    private Set<Role> roles;


    private String site;
    private String location;
    private String trigram;
    private String ral;
    private Date ralStartDate;
    private Integer dailyAllowance;
    private Date dailyAllowanceStartDate;
    private String ccnlLevel;
    private Date ccnlLevelStartDate;
    private String note;


    @OneToMany(mappedBy = "resource", fetch = FetchType.EAGER)
    private Set<ResourceHourlyCost> hourlyCosts;

    @OneToMany(mappedBy = "resource", fetch = FetchType.EAGER)
    private Set<ResourceWorkingTime> workingTimes;

    @OneToMany(mappedBy = "resource")
    Set<ResourceSkill> resourceSkills;


    public String  getUnitTrigram(){
        return this.unit != null ? this.unit.getTrigram() : null;
    }


    public ResourceHourlyCost getMostUpToDateHourlyCost(LocalDate date){

        if (this.hourlyCosts != null)
            return this.hourlyCosts.stream()
                .filter(r -> !r.getStartDate().isAfter(date))
                .max(Comparator.comparing(ResourceHourlyCost::getStartDate)).orElse(null);
        else
            return null;
    }

    public ResourceWorkingTime getMostUpToDateWorkingTime(LocalDate date){

        if (this.workingTimes != null)
            return this.workingTimes.stream()
                    .filter(r -> !r.getStartDate().isAfter(date))
                    .max(Comparator.comparing(ResourceWorkingTime::getStartDate)).orElse(null);
        else
            return null;
    }

    public Float getCurrentHourlyCost(){
        ResourceHourlyCost resourceHourlyCost = getMostUpToDateHourlyCost(LocalDate.now());
        return resourceHourlyCost != null ? resourceHourlyCost.getCost() : null;
    }


    @Override
    public Resource clone() {
        try {
            Resource cloned = (Resource) super.clone();
            // Cloning mutable fields
            cloned.setUnit(this.getUnit() != null ? this.getUnit().clone() : null);
            // Cloning collections
            cloned.setRoles(new HashSet<>(this.getRoles()));
            return (Resource) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported for Resource");
        }
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", hiringDate=" + hiringDate +
                ", leaveDate=" + leaveDate +
                ", lastWorkingTime=" + lastWorkingTime +
                ", lastWorkingTimeStartDate=" + lastWorkingTimeStartDate +
                ", unitId=" + (unit != null ? unit.getId() : null) +
                ", lastHourlyCost=" + lastHourlyCost +
                ", lastHourlyCostStartDate=" + lastHourlyCostStartDate +
                ", roles=" + roles +
                ", site='" + site + '\'' +
                ", location='" + location + '\'' +
                ", trigram='" + trigram + '\'' +
                ", ral='" + ral + '\'' +
                ", ralStartDate=" + ralStartDate +
                ", dailyAllowance=" + dailyAllowance +
                ", dailyAllowanceStartDate=" + dailyAllowanceStartDate +
                ", ccnlLevel='" + ccnlLevel + '\'' +
                ", ccnlLevelStartDate=" + ccnlLevelStartDate +
                ", note='" + note + '\'' +
                '}';
    }


    @Override
    public int compareTo(Resource otherResource) {
        return this.employeeId.compareTo(otherResource.getEmployeeId());
    }

}
