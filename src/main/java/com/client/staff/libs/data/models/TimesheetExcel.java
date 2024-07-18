package com.client.staff.libs.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimesheetExcel {

    private String unitTrigram;
    private String projectName;
    private String projectId;
    private Integer resourceEmployeeId;
    private String resourceName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate month;
    private Integer hours;
}
