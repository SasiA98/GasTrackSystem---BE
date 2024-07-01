package com.teoresi.staff.dtos.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TimesheetDTO {

    @EqualsAndHashCode.Include
    private long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totWorkHours;
    private int remainingWorkHours;
    private Set<TimesheetProjectDTO> relatedProjects;
}
