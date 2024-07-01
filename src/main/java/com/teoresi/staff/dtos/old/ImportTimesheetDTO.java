package com.teoresi.staff.dtos.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ImportTimesheetDTO {

    private Integer totalRows;
    private Integer processedRows;
    private Integer totalResources;
    private Integer processedResources;
    private Integer totalProjects;
    private Integer processedProjects;
    private int progress;
    private String message;
}
