package com.teoresi.staff.dtos.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teoresi.staff.shared.models.Role;
import lombok.*;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResourceDTO {

    @EqualsAndHashCode.Include
    private long id;
    private Integer employeeId;
    private String email;
    private String name;
    private String surname;
    private Date birthDate;
    private Date hiringDate;
    private Date leaveDate;
    private UnitDTO unit;
    private String unitName;
    private String site;
    private String location;
    private String status;
    private Float lastHourlyCost;
    private Float currentHourlyCost;
    private Date lastHourlyCostStartDate;
    private Set<Role> roles;
    private String trigram;
    private String ral;
    private Date ralStartDate;
    private int lastWorkingTime;
    private Date lastWorkingTimeStartDate;
    private Integer dailyAllowance;
    private Date dailyAllowanceStartDate;
    private String ccnlLevel;
    private Date ccnlLevelStartDate;
    private String note;
}
