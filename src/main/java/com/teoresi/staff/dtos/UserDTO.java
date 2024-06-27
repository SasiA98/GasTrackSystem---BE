package com.teoresi.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teoresi.staff.shared.models.Role;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDTO {

    @EqualsAndHashCode.Include
    private Long id;

    private ResourceDTO resource;
    private String name;
    private String surname;
    private Set<Role> roles;
    private String status;

}
