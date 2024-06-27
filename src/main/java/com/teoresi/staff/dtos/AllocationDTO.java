package com.teoresi.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teoresi.staff.shared.models.Role;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AllocationDTO {

    @EqualsAndHashCode.Include
    private Long id;
    private String resourceName;
    private String projectName;
    private Long projectId;
    private Long resourceId;
    private boolean isRealCommitment;
    private Integer commitmentPercentage;
    private Integer hours;
    private Date startDate;
    private Date endDate;
    private Role role;

}
