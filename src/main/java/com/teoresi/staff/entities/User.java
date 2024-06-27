package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import com.teoresi.staff.shared.models.Role;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Table(name = "user")
public class User extends BasicEntity {

    private String password;

    @OneToOne
    @JoinColumn
    private Resource resource;

    public Long getResourceId(){ return this.resource != null ? this.resource.getId() : null; }

    public String getName(){ return this.resource != null ? this.resource.getName() : null; }
    public String getEmail(){ return this.resource != null ? this.resource.getEmail() : null; }

    public String getSurname(){ return this.resource != null ? this.resource.getSurname() : null; }

    public Set<Role> getRoles(){ return this.resource != null ? this.resource.getRoles() : null; }

    private String status;


    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() + // Assuming getId() is inherited from BasicEntity
                ", password='" + password + '\'' +
                ", resourceId=" + getResourceId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", roles=" + getRoles() +
                ", status='" + status + '\'' +
                '}';
    }
}
