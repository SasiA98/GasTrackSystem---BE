package com.teoresi.staff.entities.old;

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
@Table(name = "skill")
public class Skill extends BasicEntity {

    @ManyToOne
    @JoinColumn(name="skill_group_id", nullable=false)
    private SkillGroup skillGroup;

    @NotBlank(message = "Skill name cannot be null or empty")
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "skill")
    Set<ResourceSkill> resourceSkills;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Skill skill = (Skill) o;
        return getId() != null && Objects.equals(getId(), skill.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skillGroup=" + (skillGroup != null ? skillGroup.getName() : null) +
                '}';
    }
}
