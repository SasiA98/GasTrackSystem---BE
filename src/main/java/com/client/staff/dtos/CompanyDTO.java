package com.client.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyDTO {

    @EqualsAndHashCode.Include
    private long id;
    private String name;
    private String owner;
    private String regione;
    private String provincia;
    private String citta;
    private String address;
    private String code;
    private String firstEmail;
    private String secondEmail;
    private String thirdEmail;
    private String phone;
    private String note;
}
