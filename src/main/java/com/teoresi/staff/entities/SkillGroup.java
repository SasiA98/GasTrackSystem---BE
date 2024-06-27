package com.teoresi.staff.entities;

import com.teoresi.staff.shared.entities.BasicEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skill_group")
public class SkillGroup extends BasicEntity {

    @NotBlank(message = "Skill Group name cannot be null or empty")
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy="skillGroup")
    private Set<Skill> skills;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        SkillGroup skill = (SkillGroup) o;
        return getId() != null && Objects.equals(getId(), skill.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "SkillGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
