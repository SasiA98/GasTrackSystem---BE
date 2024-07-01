package com.teoresi.staff.dtos.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TimesheetProjectDTO {

    @EqualsAndHashCode.Include
    private long id;

    private Float estimatedHrCost;
    private Float actualHrCost;
    private String name;
    private Long projectId;
    private Long timesheetId;
    private Long allocationId;
    private Integer hours;
    private Integer preImportHours;
    private String note;
    private String proposedWorkHours;
    private Integer allocationHours;
    private Float cost;
    private Boolean verifiedHours;

}
