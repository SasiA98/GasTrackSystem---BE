package com.teoresi.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProjectDTO {

    @EqualsAndHashCode.Include
    private long id;
    private String name;
    private String industry;
    private String presaleTrigram;
    private Long presaleId;
    private String dumTrigram;
    private Long dumId;
    private Long unitId;
    private String pmTrigram;
    private String bmTrigram;
    private String status;
    private String crmCode;
    private String projectId;
    private boolean ic;
    private boolean isSpecial;
    private Date startDate;
    private Date preSaleScheduledEndDate;
    private Date estimatedEndDate;
    private Date komDate;
    private Float actualCost;
    private Float currentEstimatedCost;
    private Float preSaleEstimatedCost;
    private Float preSaleFixedCost;
    private Float currentFixedCost;
    private Float currentEstimatedHrCost;
    private Float preSaleEstimatedHrCost;
    private Date endDate;
    private String note;
    private String projectType;
}
