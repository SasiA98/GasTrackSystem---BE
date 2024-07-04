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
public class CompanyLicenceDTO {

    @EqualsAndHashCode.Include
    private long id;
    private CompanyDTO company;
    private String licenceName;
    private Date expiryDate;
}
