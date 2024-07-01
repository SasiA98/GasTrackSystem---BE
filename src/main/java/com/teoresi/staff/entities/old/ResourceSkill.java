package com.teoresi.staff.entities.old;

import com.teoresi.staff.libs.data.models.IdentifiableEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "resource_skill")
public class ResourceSkill implements IdentifiableEntity<ResourceSkillKey> {

    @EmbeddedId
    private ResourceSkillKey id;

    @ManyToOne
    @MapsId("resourceId")
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private int rating;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ResourceSkill that = (ResourceSkill) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public String toString() {
        return "ResourceSkill{" +
                "id=" + id +
                ", rating=" + rating +
                '}';
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
