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
public class ResourceHourlyCostDTO {

    @EqualsAndHashCode.Include
    private long id;
    private float cost;
    private long resourceId;
    private Date startDate;
}
