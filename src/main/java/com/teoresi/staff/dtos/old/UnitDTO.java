package com.teoresi.staff.dtos.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UnitDTO {

    @EqualsAndHashCode.Include
    private long id;
    private String trigram;
    private String type;
    private String status;
}
