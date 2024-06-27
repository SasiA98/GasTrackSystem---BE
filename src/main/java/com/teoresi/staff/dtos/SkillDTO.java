package com.teoresi.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SkillDTO {

    @EqualsAndHashCode.Include
    private long id;
    private SkillGroupDTO skillGroup;
    private String name;
}
