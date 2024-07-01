package com.teoresi.staff.dtos.old.CustomsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResourcesSalaryDetailDTO {

    @EqualsAndHashCode.Include
    private long id;
    private String ral;
    private Date ralStartDate;
    private Integer dailyAllowance;
    private Date dailyAllowanceStartDate;
    private String ccnlLevel;
    private Date ccnlLevelStartDate;
    private long resource_id;


}
