package com.teoresi.staff.entities.old;

import com.teoresi.staff.shared.entities.BasicEntity;
import com.teoresi.staff.shared.models.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "project")
public class Project extends BasicEntity implements Cloneable, Comparable<Project>{

    private Long id;

    private String name;
    private String industry;

    @ManyToOne
    @JoinColumn
    private Resource PRESALE;

    @ManyToOne
    @JoinColumn
    private Resource DUM;

    @ManyToOne
    @JoinColumn
    private Unit unit;

    @Transient
    public Resource getPM() {
        return getPMForAllocations(true);
    }

    public Resource getPMForAllocations(boolean isRealCommitment) {
        if (allocations != null) {
            for (Allocation member : allocations) {
                LocalDate startDate = member.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate currentDate = LocalDate.now();

                if (member.getEndDate() != null) {
                    LocalDate endDate = member.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    if (isRealCommitment) {
                        if (member.getRole() == Role.PM &&
                            member.isRealCommitment() &&
                            !endDate.isBefore(currentDate) &&
                            !startDate.isAfter(currentDate)) {
                            return member.getResource();
                        }
                    } else {
                        if (member.getRole() == Role.PM &&
                                !member.isRealCommitment() &&
                                !endDate.isBefore(currentDate) &&
                                !startDate.isAfter(currentDate)) {
                            return member.getResource();
                        }
                    }
                } else if(!startDate.isAfter(currentDate))
                    return member.getResource();
            }
        }
        return null;
    }


    private String bmTrigram;
    private String status;
    private String crmCode;
    private String projectId;
    private boolean ic;
    private boolean isSpecial;
    private Date startDate;
    private Date estimatedEndDate;
    private Date preSaleScheduledEndDate;
    private Date komDate;
    private Date endDate;
    private String note;
    private Float preSaleFixedCost;
    private Float currentFixedCost;
    private String projectType;
    // private Float dailyEstimatedCostQuota; // computed

    @OneToOne(mappedBy = "project")
    private ProjectCosts projectCosts;


    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    Set<Allocation> allocations;


    public Float getActualHrCost(){
        return projectCosts != null ? projectCosts.getActHrCost() : null;
    }

    public Float getPreSaleEstimatedHrCost(){
        return projectCosts != null ? projectCosts.getPresaleEstimatedHrCost() : null;
    }
    public Float getCurrentEstimatedHrCost(){
        return projectCosts != null ? projectCosts.getCurrentEstimatedHrCost() : null;
    }

    public Float getCurrentEstimatedCost(){
        float fixedCost = (this.currentFixedCost != null) ? this.currentFixedCost : 0.0f;
        float hrCost =  (getCurrentEstimatedHrCost() != null) ? getCurrentEstimatedHrCost() : 0.0f;
        return fixedCost + hrCost;
    }

    public Float getPreSaleEstimatedCost(){
        float fixedCost = (this.preSaleFixedCost != null) ? this.preSaleFixedCost : 0.0f;
        float hcCost =  (getPreSaleEstimatedHrCost() != null) ? getPreSaleEstimatedHrCost() : 0.0f;
        return fixedCost + hcCost;
    }

    public Float getActualCost(){
        if(status.equals("Pre-Sale")) {
            return 0.0f;
        } else {
            float fixedCost = (this.currentFixedCost != null) ? this.currentFixedCost : 0.0f;
            float hcCost =  (getActualHrCost() != null) ? getActualHrCost() : 0.0f;
            return fixedCost + hcCost;
        }
    }

    public Float getPreSaleFixedCost(){
        return this.preSaleFixedCost != null ? this.preSaleFixedCost : 0.0f;
    }

    public Float getCurrentFixedCost(){
        return this.currentFixedCost != null ? this.currentFixedCost : 0.0f;
    }


    public String getPresaleTrigram(){
        if (this.PRESALE==null)
            return "";

        return this.PRESALE.getTrigram() != null ? this.PRESALE.getTrigram() : this.PRESALE.getSurname();
    }



    public Long getPresaleId(){
        return this.getPRESALE() != null ? this.getPRESALE().getId() : null;
    }

    public Long getDumId(){
        return this.getDUM() != null ? this.getDUM().getId() : null;
    }

    public Long getUnitId(){
        return this.getUnit() != null ? this.getUnit().getId() : null;
    }

    public String getDumTrigram(){
        if (this.DUM == null)
            return "";

        return this.DUM.getTrigram() != null ? this.DUM.getTrigram() : this.DUM.getSurname();
    }

    @NonNull
    public String getPmTrigram(){
        if (this.getPM()==null)
            return "";

        return this.getPM().getTrigram() != null ? this.getPM().getTrigram() : this.getPM().getSurname();
    }


    @Override
    public Project clone() {
        try {
            Project clonedProject = (Project) super.clone();

            if (this.PRESALE != null) {
                clonedProject.setPRESALE(this.PRESALE.clone());
            }
            if (this.DUM != null) {
                clonedProject.setDUM(this.DUM.clone());
            }

            return clonedProject;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", industry='" + industry + '\'' +
                ", presaleId=" + (PRESALE != null ? PRESALE.getId() : null) +
                ", dumId=" + (DUM != null ? DUM.getId() : null) +
                ", unitId=" + (unit != null ? unit.getId() : null) +
                ", bmTrigram='" + bmTrigram + '\'' +
                ", status='" + status + '\'' +
                ", crmCode='" + crmCode + '\'' +
                ", projectId='" + projectId + '\'' +
                ", ic=" + ic +
                ", isSpecial=" + isSpecial +
                ", startDate=" + startDate +
                ", estimatedEndDate=" + estimatedEndDate +
                ", preSaleScheduledEndDate=" + preSaleScheduledEndDate +
                ", komDate=" + komDate +
                ", endDate=" + endDate +
                ", note='" + note + '\'' +
                ", preSaleFixedCost=" + preSaleFixedCost +
                ", currentFixedCost=" + currentFixedCost +
                ", projectType='" + projectType + '\'' +
                ", projectCostsId=" + (projectCosts != null ? projectCosts.getId() : null) +
                '}';
    }

    @Override
    public int compareTo(Project otherProject) {
        return this.id.compareTo(otherProject.getId());
    }
}
