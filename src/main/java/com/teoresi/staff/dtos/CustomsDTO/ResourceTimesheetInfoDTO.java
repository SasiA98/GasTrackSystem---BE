package com.teoresi.staff.dtos.CustomsDTO;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceTimesheetInfoDTO {

    private String unitTrigram;
    private Long unitId;
    private Long resourceId;
    private String resourceName;
    private String resourceSurname;
    private int remainingWorkHours;
}

