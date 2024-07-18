package com.client.staff.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.client.staff.shared.models.Role;
import lombok.*;
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
    private String email;
    private String name;
    private String surname;
    private Set<Role> roles;
}
