package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.SkillGroup;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillGroupRepository extends CrudRepository<SkillGroup, Long> {
}
