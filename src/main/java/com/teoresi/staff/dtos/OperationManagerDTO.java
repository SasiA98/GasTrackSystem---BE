package com.teoresi.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OperationManagerDTO {

    @EqualsAndHashCode.Include
    private Long id;
    private String legalEntity;
    private String industry;
    private String name;
    private String trigram;
    private String roles;
    private String reportsTo;
}
