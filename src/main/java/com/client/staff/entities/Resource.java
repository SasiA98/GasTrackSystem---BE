package com.client.staff.entities;

import com.client.staff.shared.entities.BasicEntity;
import com.client.staff.shared.models.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity(name = "resource")
public class Resource extends BasicEntity {

    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String surname;

    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "resources_roles", joinColumns = @JoinColumn(name = "resource_id"))
    private Set<Role> roles;


    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id;
    }

}
