package com.client.staff.dtos;

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
    private LicenceDTO licence;
    private String isEmailSent;
    private Date expiryDate;
}
