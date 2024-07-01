package com.teoresi.staff.dtos.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ImportResourceDTO {

    private Integer totalRows;
    private Integer processedRows;
    private Integer processedResources;
    private Integer totalResources;
    private int progress;
    private String message;
}
